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
fun findAllJUnit4Tests(dexFiles: List<DexFile>, customAnnotations: List<String>): List<TestMethod> {

    // Map to hold all the class information we've found as we go
    // From the docs:
    // The classes must be ordered such that a given class's superclass and
    // implemented interfaces appear in the list earlier than the referring class
    // BUT it's not true for multiple dex files: superclass can be located in a different dex file
    // since the order is not guaranteed in this case we need to traverse all superclasses for each class
    val classTestMethods: Map<String, ClassParsingResult> = dexFiles.parseClasses(customAnnotations)

    // Map for the second iteration to cache all found test methods including methods from superclass
    val classAllTestMethods: MutableMap<String, Set<TestMethod>> = hashMapOf()

    return classTestMethods
            .values
            .filter { it.isConcrete }
            .map { value -> createAllTestMethods(value, classTestMethods, classAllTestMethods) }
            .flatten()
}

private fun List<DexFile>.parseClasses(customAnnotations: List<String>): Map<String, ClassParsingResult> =
        asSequence()
                .flatMap { dexFile ->
                    // We include classes that do not have annotations because there may be an intermediary class without tests
                    // For example, TestClass1 defines a test, EmptyClass2 extends TestClass1 and defines nothing, and then TestClass2
                    // extends EmptyClass2, TestClass2 should also list the tests defined in TestClass1
                    dexFile
                            .classDefs
                            .asSequence()
                            .filterNot(ClassDefItem::isInterface)
                            .map { classDef ->
                                val testMethods = dexFile
                                        .createTestMethods(classDef, dexFile.findMethodIds())
                                        .filter { it.containsTestAnnotation(customAnnotations) }

                                ClassParsingResult(
                                        dexFile = dexFile,
                                        classDef = classDef,
                                        className = dexFile.getClassName(classDef),
                                        superClassName = dexFile.getSuperclassName(classDef),
                                        testMethods = testMethods.toSet(),
                                        isConcrete = classDef.isConcrete
                                )
                            }
                }
                .associateBy { it.className }

private const val JUNIT_TEST_ANNOTATION_NAME = "org.junit.Test"

private fun TestMethod.containsTestAnnotation(customAnnotations: List<String>): Boolean {
    for (a in customAnnotations) {
        if (annotations.map { it.name }.contains(a)) {
            return true
        }
    }
    if (annotations.map { it.name }.contains(JUNIT_TEST_ANNOTATION_NAME)) {
        return true
    }
    return false
}

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
private fun createAllTestMethods(
    parsingResult: ClassParsingResult,
    classTestMethods: Map<String, ClassParsingResult>,
    classAllTestMethods: MutableMap<String, Set<TestMethod>>
): Set<TestMethod> =
        classAllTestMethods.getOrPut(parsingResult.className) {
            val dexFile = parsingResult.dexFile

            val superTestMethods = classTestMethods[parsingResult.superClassName]
                    ?.let { createAllTestMethods(it, classTestMethods, classAllTestMethods) }
                    ?: emptySet()

            val className = dexFile.formatClassName(parsingResult.classDef)
            val directory = dexFile.getAnnotationsDirectory(parsingResult.classDef)
            val childClassAnnotations = dexFile.getClassAnnotationValues(directory)
            val childClassAnnotationNames = childClassAnnotations.map { it.name }

            val adaptedSuperMethods = superTestMethods
                    .map { method ->
                        val onlyParentAnnotations = method
                                .annotations
                                .filterNot { childClassAnnotationNames.contains(it.name) }
                                .filter { it.inherited }

                        TestMethod(
                                testName = className + method.testNameWithoutClass,
                                annotations = onlyParentAnnotations + childClassAnnotations
                        )
                    }
                    .toSet()

            return adaptedSuperMethods union parsingResult.testMethods
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
