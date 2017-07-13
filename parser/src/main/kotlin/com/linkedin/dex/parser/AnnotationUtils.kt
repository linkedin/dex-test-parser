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
 * @return A list of descriptors containing all class-level annotations in the given [AnnotationsDirectoryItem]
 */
fun DexFile.getClassAnnotationDescriptors(directory: AnnotationsDirectoryItem?): List<String> {
    if (directory == null || directory.classAnnotationsOff == 0) {
        return emptyList()
    }

    val classAnnotationSetItem = AnnotationSetItem.create(byteBuffer, directory.classAnnotationsOff)
    return classAnnotationSetItem.toDescriptorList(this)
}

/**
 * @return A list of descriptors containing all the method-level annotations on the given [MethodIdItem]
 */
fun DexFile.getMethodAnnotationDescriptors(methodId: MethodIdItem,
                                           annotationsDirectory: AnnotationsDirectoryItem?): List<String> {
    // find the annotated method matching the given one, otherwise return an empty list
    val methodAnnotations = annotationsDirectory?.methodAnnotations ?: emptyArray<MethodAnnotation>()
    return methodAnnotations.filter { methodIds[it.methodIdx] == methodId }
            .flatMap { (_, annotationsOff) ->
                val methodAnnotationSetItem = AnnotationSetItem.create(byteBuffer, annotationsOff)
                methodAnnotationSetItem.toDescriptorList(this)
            }
}

/**
 * Merge the given [methodAnnotationDescriptors] and [classAnnotationDescriptors] lists and format them as
 * human-readable names
 */
fun getAnnotationNames(methodAnnotationDescriptors: List<String>,
                       classAnnotationDescriptors: List<String>): List<String> {
    return methodAnnotationDescriptors
            .plus(classAnnotationDescriptors)
            .distinct()
            .map(::formatDescriptor)
}

private fun AnnotationSetItem.toDescriptorList(dexFile: DexFile): List<String> {
    return entries
            .map { AnnotationItem.create(dexFile.byteBuffer, it.annotationOff) }
            .map { (_, encodedAnnotation) ->
                ParseUtils.parseDescriptor(dexFile.byteBuffer,
                        dexFile.typeIds[encodedAnnotation.typeIdx],
                        dexFile.stringIds)
            }
}
