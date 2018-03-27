/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.parser

import com.linkedin.dex.spec.ACC_INTERFACE
import com.linkedin.dex.spec.AnnotationsDirectoryItem
import com.linkedin.dex.spec.ClassDefItem
import com.linkedin.dex.spec.DexFile
import com.linkedin.dex.spec.MethodIdItem

/**
 * Find all methods that are annotated with JUnit4's @Test annotation
 */
fun DexFile.findJUnit4Tests(): List<TestMethod> {
    val testAnnotationName = "org.junit.Test"
    val classesWithAnnotations = classDefs.filter(::hasAnnotations).filterNot(::isInterface)

    return createTestMethods(classesWithAnnotations, findMethodIds())
            .filter { it.annotations.map { it.name }.contains(testAnnotationName) }
}

/**
 * Find methodIds we care about: any method in the class which is annotated
 */
private fun DexFile.findMethodIds(): (ClassDefItem, AnnotationsDirectoryItem?) -> List<MethodIdItem> {
    return { _, directory -> directory?.methodAnnotations?.map { methodIds[it.methodIdx] } ?: emptyList() }
}

private fun isInterface(classDefItem: ClassDefItem): Boolean {
    return classDefItem.accessFlags and ACC_INTERFACE == ACC_INTERFACE
}
