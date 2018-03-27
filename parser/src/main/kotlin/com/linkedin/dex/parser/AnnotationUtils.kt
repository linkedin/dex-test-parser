/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.parser

import com.linkedin.dex.spec.*

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

    val values = classAnnotationSetItem.entries.map { AnnotationItem.create(byteBuffer, it.annotationOff) }
            .map {
                val name = formatDescriptor(ParseUtils.parseDescriptor(byteBuffer,
                    typeIds[it.encodedAnnotation.typeIdx], stringIds))
                val encodedAnnotationValues = it.encodedAnnotation.elements
                val values = HashMap<String, DecodedValue>()
                for (encodedAnnotationValue in encodedAnnotationValues) {
                    val value = DecodedValue.createFromDecodedValue(this, encodedAnnotationValue.value)
                    byteBuffer.position(this.stringIds[encodedAnnotationValue.nameIdx].stringDataOff)
                    val name = ParseUtils.parseStringBytes(byteBuffer)

                    values.put(name, value)
                }

                TestAnnotation(name, values)
            }

    return values
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


    val allAnnotations = annotationSets.map {
        it.entries.map { AnnotationItem.create(byteBuffer, it.annotationOff) }
            .map {
                val name = formatDescriptor(ParseUtils.parseDescriptor(byteBuffer,
                        typeIds[it.encodedAnnotation.typeIdx], stringIds))
                val encodedAnnotationValues = it.encodedAnnotation.elements
                val values = HashMap<String, DecodedValue>()
                for (encodedAnnotationValue in encodedAnnotationValues) {
                    val value = DecodedValue.createFromDecodedValue(this, encodedAnnotationValue.value)
                    byteBuffer.position(this.stringIds[encodedAnnotationValue.nameIdx].stringDataOff)
                    val name = ParseUtils.parseStringBytes(byteBuffer)

                    values.put(name, value)
                }

                TestAnnotation(name, values)
            }
    }

    return allAnnotations.flatten()
}

private fun AnnotationSetItem.getEncodedAnnotations(dexFile: DexFile): List<EncodedAnnotation> {
    return entries
            .map { AnnotationItem.create(dexFile.byteBuffer, it.annotationOff) }
            .map { (_, encodedAnnotation) ->
                encodedAnnotation
            }
}
