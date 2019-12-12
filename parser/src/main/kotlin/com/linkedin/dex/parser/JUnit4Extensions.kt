/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.parser

import com.linkedin.dex.spec.ACC_ABSTRACT
import com.linkedin.dex.spec.ACC_INTERFACE
import com.linkedin.dex.spec.AnnotationsDirectoryItem
import com.linkedin.dex.spec.ClassDefItem
import com.linkedin.dex.spec.DexFile
import com.linkedin.dex.spec.MethodIdItem

/**
 * Find all methods that are annotated with JUnit4's @Test annotation, including any test methods that
 * may be inherited from superclasses or interfaces.
 */
fun findAllJUnit4Tests(dexFiles: List<DexFile>): List<TestMethod> {

    // Map to hold all the class information we've found as we go
    // From the docs:
    // The classes must be ordered such that a given class's superclass and
    // implemented interfaces appear in the list earlier than the referring class
    // BUT it's not true for multiple dex files: superclass can be located in a different dex file
    // since the order is not guaranteed in this case we need to traverse all superclasses for each class
    val classTestMethods: MutableMap<String, ClassParsingResult> = mutableMapOf()

    // Map for the second iteration to cache all found test methods including methods from superclass
    val classAllTestMethods: MutableMap<String, Set<TestMethod>> = hashMapOf()

    dexFiles
            .forEach { dexFile ->

                // We include classes that do not have annotations because there may be an intermediary class without tests
                // For example, TestClass1 defines a test, EmptyClass2 extends TestClass1 and defines nothing, and then TestClass2
                // extends EmptyClass2, TestClass2 should also list the tests defined in TestClass1
                dexFile
                        .classDefs
                        .asSequence()
                        .filterNot(ClassDefItem::isInterface)
                        .forEach { classDef ->
                            val testMethods = dexFile
                                    .createTestMethods(classDef, dexFile.findMethodIds())
                                    .filter { it.containsTestAnnotation }

                            classTestMethods[dexFile.getClassName(classDef)] = ClassParsingResult(
                                    dexFile = dexFile,
                                    classDef = classDef,
                                    className = dexFile.getClassName(classDef),
                                    superClassName = dexFile.getSuperclassName(classDef),
                                    testMethods = testMethods.toSet(),
                                    isConcrete = classDef.isConcrete
                            )
                        }
            }

    classTestMethods
            .keys
            .forEach { key ->
                classTestMethods.computeIfPresent(key) { _, value ->
                    val testMethods = createTestMethodsFromSuperMethods(value, classTestMethods, classAllTestMethods)
                    value.copy(testMethods = value.testMethods union testMethods)
                }
            }

    return classTestMethods
            .values
            .filter { it.isConcrete }
            .flatMap { it.testMethods }
            .toList()
}

private const val JUNIT_TEST_ANNOTATION_NAME = "org.junit.Test"

private val TestMethod.containsTestAnnotation: Boolean
    get() = annotations.map { it.name }.contains(JUNIT_TEST_ANNOTATION_NAME)

/**
 * Gets the superclass' test methods, so they can be transferred into the subclass as well
 *
 * Because we build the parsed classes map with the full list of test methods for a given class (including super methods),
 * we don't need to actually traverse up the tree here. The immediate superclass will contain all other methods in it
 * already
 */
private fun getSuperTestMethods(classTestMethods: Map<String, ClassParsingResult>,
                                superClass: String): Set<TestMethod> =
        classTestMethods[superClass]?.testMethods ?: emptySet()

/**
 * Find methodIds we care about: any method in the class which is annotated
 */
private fun DexFile.findMethodIds(): (ClassDefItem, AnnotationsDirectoryItem?) -> List<MethodIdItem> {
    return { classDefItem, directory ->
        val annotatedIds = directory?.methodAnnotations?.map { it.methodIdx } ?: emptyList()
        this.findMethodIdxs(classDefItem).filter {
            annotatedIds.contains(it)
        }.map { methodIds[it] }
    }
}

private fun DexFile.getClassName(classDefItem: ClassDefItem): String {
    return ParseUtils.parseClassName(byteBuffer, classDefItem, typeIds, stringIds)
}

private fun DexFile.getSuperclassName(classDefItem: ClassDefItem): String {
    val superClassIdx = classDefItem.superclassIdx
    val typeId = typeIds[superClassIdx]

    return ParseUtils.parseDescriptor(byteBuffer, typeId, stringIds)
}

/**
 * Creates new TestMethod objects with the class name changed from the super class to the subclass
 */
private fun createTestMethodsFromSuperMethods(parsingResult: ClassParsingResult,
                                              classTestMethods: Map<String, ClassParsingResult>,
                                              classAllTestMethods: MutableMap<String, Set<TestMethod>>): Set<TestMethod> =
        classAllTestMethods.getOrPut(parsingResult.className) {
            val dexFile = parsingResult.dexFile

            val superTestMethods = getSuperTestMethods(classTestMethods, parsingResult.superClassName)
            val superSuperTestMethods = classTestMethods[parsingResult.superClassName]
                    ?.superClassName
                    ?.let { classTestMethods[it] }
                    ?.let { createTestMethodsFromSuperMethods(it, classTestMethods, classAllTestMethods) }
                    ?: emptySet()

            val testMethods = superTestMethods + superSuperTestMethods

            val className = dexFile.formatClassName(parsingResult.classDef)
            val directory = dexFile.getAnnotationsDirectory(parsingResult.classDef)
            val childClassAnnotations = dexFile.getClassAnnotationValues(directory)
            val childClassAnnotationNames = childClassAnnotations.map { it.name }

            return testMethods
                    .map { method ->
                        val onlyParentAnnotations = method
                                .annotations
                                .filterNot { childClassAnnotationNames.contains(it.name) }
                                .filter { it.inherited }

                        TestMethod(className + method.testNameWithoutClass, onlyParentAnnotations + childClassAnnotations)
                    }
                    .toSet()
        }

private val TestMethod.testNameWithoutClass
    get() = testName.substring(testName.indexOf('#') + 1)

private val ClassDefItem.isConcrete: Boolean
    get() = !isAbstract && !isInterface

private val ClassDefItem.isInterface: Boolean
    get() = accessFlags and ACC_INTERFACE == ACC_INTERFACE

private val ClassDefItem.isAbstract: Boolean
    get() = accessFlags and ACC_ABSTRACT == ACC_ABSTRACT

// Class to hold the information we have parsed about the classes we have already seen
// We need to hold the information for every class, since there is no way to know if a later class will subclass it or not
// We keep isConcrete as well to make filtering at the end easier
private data class ClassParsingResult(
        val dexFile: DexFile,
        val classDef: ClassDefItem,
        val className: String,
        val superClassName: String,
        val testMethods: Set<TestMethod>,
        val isConcrete: Boolean
)
