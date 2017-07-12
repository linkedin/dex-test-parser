/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.spec

import java.nio.ByteBuffer

data class AnnotationsDirectoryItem(
        val classAnnotationsOff: Int,
        val fieldsSize: Int,
        val annotatedMethodsSize: Int,
        val annotatedParametersSize: Int,
        val fieldAnnotations: Array<FieldAnnotation>,
        val methodAnnotations: Array<MethodAnnotation>,
        val parameterAnnotations: Array<ParameterAnnotation>
) {
    companion object {
        fun create(byteBuffer: ByteBuffer, offset: Int): AnnotationsDirectoryItem {
            byteBuffer.position(offset)

            val classAnnotationsOff = byteBuffer.int
            val fieldsSize = byteBuffer.int
            val annotatedMethodsSize = byteBuffer.int
            val annotatedParametersSize = byteBuffer.int
            val fieldAnnotations = Array(fieldsSize, { FieldAnnotation(byteBuffer) })
            val methodAnnotations = Array(annotatedMethodsSize, { MethodAnnotation(byteBuffer) })
            val parameterAnnotations = Array(annotatedParametersSize, { ParameterAnnotation(byteBuffer) })
            return AnnotationsDirectoryItem(
                    classAnnotationsOff,
                    fieldsSize,
                    annotatedMethodsSize,
                    annotatedParametersSize,
                    fieldAnnotations,
                    methodAnnotations,
                    parameterAnnotations
            )
        }
    }
}
