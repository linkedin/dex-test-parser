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
import com.linkedin.dex.spec.MethodIdItem


/**
 * Check if there are any class, field, method, or parameter annotations in the given class
 *
 * @return true if this [ClassDefItem] has any annotations, false otherwise
 */
fun hasAnnotations(classDefItem: ClassDefItem): Boolean {
    return classDefItem.annotationsOff != 0
}

/**
 * Null-safe creation of an [AnnotationsDirectoryItem]
 */
fun DexFile.getAnnotationsDirectory(classDefItem: ClassDefItem): AnnotationsDirectoryItem? {
    if (hasAnnotations(classDefItem)) {
        return AnnotationsDirectoryItem.create(byteBuffer, classDefItem.annotationsOff)
    } else {
        return null
    }
}

/**
 * @return a list of annotation objects containing all class-level annotations
 */
fun DexFile.getClassAnnotationValues(directory: AnnotationsDirectoryItem?): List<TestAnnotation> {
    if (directory == null || directory.classAnnotationsOff == 0) {
        return emptyList()
    }

    val classAnnotationSetItem = AnnotationSetItem.create(byteBuffer, directory.classAnnotationsOff)

    return classAnnotationSetItem.entries.map { AnnotationItem.create(byteBuffer, it.annotationOff) }.map { getTestAnnotation(it) }
}

/**
 * @return A list of annotation objects for all the method-level annotations
 */
fun DexFile.getMethodAnnotationValues(methodId: MethodIdItem, annotationsDirectory: AnnotationsDirectoryItem?): List<TestAnnotation> {
    val methodAnnotations = annotationsDirectory?.methodAnnotations ?: emptyArray<MethodAnnotation>()
    val annotationSets = methodAnnotations.filter { methodIds[it.methodIdx] == methodId }
            .map { (_, annotationsOff) ->
                AnnotationSetItem.create(byteBuffer, annotationsOff)
            }

    return annotationSets.map {
        it.entries.map { AnnotationItem.create(byteBuffer, it.annotationOff) }.map { getTestAnnotation(it) }
    }.flatten()
}

fun DexFile.getTestAnnotation(annotationItem: AnnotationItem): TestAnnotation {
    val name = formatDescriptor(ParseUtils.parseDescriptor(byteBuffer,
            typeIds[annotationItem.encodedAnnotation.typeIdx], stringIds))
    val encodedAnnotationValues = annotationItem.encodedAnnotation.elements
    val values = mutableMapOf<String, DecodedValue>()
    for (encodedAnnotationValue in encodedAnnotationValues) {
        val value = DecodedValue.create(this, encodedAnnotationValue.value)
        val valueName = ParseUtils.parseValueName(byteBuffer, stringIds, encodedAnnotationValue.nameIdx)

        values.put(valueName, value)
    }

    val annotationClassDef = typeIdToClassDefMap[annotationItem.encodedAnnotation.typeIdx]
    val inherited = checkIfAnnotationIsInherited(annotationClassDef)

    return TestAnnotation(name, values, inherited)
}

private fun DexFile.checkIfAnnotationIsInherited(annotationClassDef: ClassDefItem?): Boolean {
    //Early return when classpath doesn't contain Ljava/lang/annotation/Inherited annotation
    if (inheritedAnnotationTypeIdIndex == null) return false

    return annotationClassDef?.let {
        val annotationsDirectory = getAnnotationsDirectory(annotationClassDef)
        if (annotationsDirectory != null && annotationsDirectory.classAnnotationsOff != 0) {
            val classAnnotationSetItem = AnnotationSetItem.create(byteBuffer, annotationsDirectory.classAnnotationsOff)
            val annotations = classAnnotationSetItem.entries.map { AnnotationItem.create(byteBuffer, it.annotationOff) }
            return@let annotations.any { it.encodedAnnotation.typeIdx == inheritedAnnotationTypeIdIndex }
        } else {
            false
        }
    } ?: false
}
