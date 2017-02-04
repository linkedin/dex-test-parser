/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.parser

import com.linkedin.dex.spec.AnnotationItem
import com.linkedin.dex.spec.AnnotationSetItem
import com.linkedin.dex.spec.AnnotationsDirectoryItem
import com.linkedin.dex.spec.ClassDefItem
import com.linkedin.dex.spec.DexFile
import com.linkedin.dex.spec.MethodAnnotation


/**
 * Find all methods that are annotated with JUnit4's @Test annotation
 */
fun DexFile.findJUnit4Tests(): List<String> {
    val testAnnotationDescriptor = "Lorg/junit/Test;"
    val matchingItems: MutableList<String> = mutableListOf()

    classDefs.filter(::hasAnnotations)
            .map { Pair(it, AnnotationsDirectoryItem.create(byteBuffer, it.annotationsOff)) }
            .map { Pair(it.first, it.second.methodAnnotations) }
            .map { Pair(it.first, getMethodsWithAnnotation(it.second, testAnnotationDescriptor)) }
            .filter { it.second.isNotEmpty() }
            .map { Pair(formatClassName(it.first), it.second) }
            .flatMap { pair -> pair.second.map { pair.first + it }.toCollection(mutableListOf()) }
            .toCollection(matchingItems)

    return matchingItems
}

private fun hasAnnotations(classDefItem: ClassDefItem): Boolean {
    return classDefItem.annotationsOff != 0
}

fun DexFile.formatClassName(classDefItem: ClassDefItem): String {
    var className = ParseUtils.parseClassName(byteBuffer, classDefItem, typeIds, stringIds)
    // strip off the "L" prefix
    className = className.substring(1)
    className = className.replace('/', '.')
    // strip off the ";"
    className = className.dropLast(1)
    // the instrument command expects test class names and method names to be separated by a "#"
    className += "#"

    return className
}

private fun DexFile.getMethodsWithAnnotation(annotations: Array<MethodAnnotation>, targetDescriptor: String): List<String> {
    val matchingMethods: MutableList<String> = mutableListOf()
    annotations.forEach { annotation ->
        val setItem = AnnotationSetItem.create(byteBuffer, annotation.annotationsOff)
        if (setItem.entries.any { entry ->
            val item = AnnotationItem.create(byteBuffer, entry.annotationOff)
            val descriptor = ParseUtils.parseDescriptor(byteBuffer, typeIds[item.encodedAnnotation.typeIdx], stringIds)
            descriptor == targetDescriptor
        }) {
            val methodIdItem = methodIds[annotation.methodIdx]
            matchingMethods.add(ParseUtils.parseMethodName(byteBuffer, stringIds, methodIdItem))
        }
    }
    return matchingMethods
}
