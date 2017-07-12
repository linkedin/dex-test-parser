/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.parser

import com.linkedin.dex.spec.AnnotationsDirectoryItem
import com.linkedin.dex.spec.ClassDefItem
import com.linkedin.dex.spec.DexFile
import com.linkedin.dex.spec.MethodIdItem

data class TestMethod(val testName: String, val annotationNames: List<String>) : Comparable<TestMethod> {
    override fun compareTo(other: TestMethod): Int = testName.compareTo(other.testName)
}

/**
 * Create the list of [TestMethod] contained in the given [ClassDefItem]s
 *
 * @param [classes] the list of [ClassDefItem]s in which to search for tests
 * @param [methodIdFinder] a function to determine which methods to consider as potential tests (varies between
 *                         JUnit3 and JUnit 4)
 */
fun DexFile.createTestMethods(
        classes: List<ClassDefItem>,
        methodIdFinder: (ClassDefItem, AnnotationsDirectoryItem?) -> List<MethodIdItem>): List<TestMethod> {
    return classes.flatMap { classDef ->
        val directory = getAnnotationsDirectory(classDef)

        // compute these outside the method loop to avoid duplicate work
        val classAnnotationDescriptors = getClassAnnotationDescriptors(directory)

        val methodIds = methodIdFinder.invoke(classDef, directory)

        methodIds.map { createTestMethod(it, directory, classDef, classAnnotationDescriptors) }
    }
}

private fun DexFile.createTestMethod(methodId: MethodIdItem,
                                     directory: AnnotationsDirectoryItem?,
                                     classDef: ClassDefItem,
                                     classAnnotationDescriptors: List<String>): TestMethod {
    val methodAnnotationDescriptors = getMethodAnnotationDescriptors(methodId, directory)

    val annotationNames = getAnnotationNames(methodAnnotationDescriptors, classAnnotationDescriptors)

    val className = formatClassName(classDef)
    val methodName = ParseUtils.parseMethodName(byteBuffer, stringIds, methodId)
    val testName = className + methodName

    return TestMethod(testName, annotationNames)
}
