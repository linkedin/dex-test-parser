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
    val testAnnotationName = "org.junit.Test"

    // Map to hold all the class information we've found as we go
    // From the docs:
    // The classes must be ordered such that a given class's superclass and
    // implemented interfaces appear in the list earlier than the referring class
    val classTestMethods: MutableMap<String, ClassParsingResult> = mutableMapOf()

    dexFiles.map { dexFile ->

        // We include classes that do not have annotations because there may be an intermediary class without tests
        // For example, TestClass1 defines a test, EmptyClass2 extends TestClass1 and defines nothing, and then TestClass2
        // extends EmptyClass2, TestClass2 should also list the tests defined in TestClass1
        val classesWithAnnotations = dexFile.classDefs.filterNot(::isInterface)

        classesWithAnnotations.map { classDef ->
            val baseTests = dexFile.createTestMethods(classDef, dexFile.findMethodIds()).filter {
                it.annotations.map {
                    it.name
                }.contains(testAnnotationName)
            }

            val superTests = createTestMethodsFromSuperMethods(dexFile, classDef, dexFile.formatClassName(classDef), getSuperTestMethods(classDef, classTestMethods, dexFile))
            classTestMethods[dexFile.getClassName(classDef)] = ClassParsingResult(dexFile.getSuperclassName(classDef), baseTests union superTests, !(isAbstract(classDef) || isInterface(classDef)))
        }
    }

    return classTestMethods.values.filter { it.isConcrete }.flatMap { it.testMethods }.toList()
}

/**
 * Gets the superclass' test methods, so they can be transferred into the subclass as well
 *
 * Because we build the parsed classes map with the full list of test methods for a given class (including super methods),
 * we don't need to actually traverse up the tree here. The immediate superclass will contain all other methods in it
 * already
 */
private fun getSuperTestMethods(classDefItem: ClassDefItem, classTestMethods: Map<String, ClassParsingResult>, dexFile: DexFile): Set<TestMethod> {
    val superClass = dexFile.getSuperclassName(classDefItem)
    return classTestMethods[superClass]?.testMethods ?: emptySet()
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
private fun createTestMethodsFromSuperMethods(dexFile: DexFile, classDefItem: ClassDefItem, className: String, superTests: Set<TestMethod>): Set<TestMethod> {
    val directory = dexFile.getAnnotationsDirectory(classDefItem)
    val childClassAnnotations = dexFile.getClassAnnotationValues(directory)
    val childClassAnnotationNames = childClassAnnotations.map { it.name }

    val tests = superTests.map { method ->
        val onlyParentAnnotations = method.annotations.filterNot { childClassAnnotationNames.contains(it.name) }
        TestMethod(className + (method.testName.substring(method.testName.indexOf('#') + 1)), onlyParentAnnotations + childClassAnnotations)
    }
    return tests.toSet()
}

private fun isInterface(classDefItem: ClassDefItem): Boolean {
    return classDefItem.accessFlags and ACC_INTERFACE == ACC_INTERFACE
}

private fun isAbstract(classDefItem: ClassDefItem): Boolean {
    return classDefItem.accessFlags and ACC_ABSTRACT == ACC_ABSTRACT
}

// Class to hold the information we have parsed about the classes we have already seen
// We need to hold the information for every class, since there is no way to know if a later class will subclass it or not
// We keep isConcrete as well to make filtering at the end easier
private data class ClassParsingResult(val superClassName: String, val testMethods: Set<TestMethod>, val isConcrete: Boolean)