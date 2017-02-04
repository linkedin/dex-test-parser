/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.spec

import java.nio.ByteBuffer

data class EncodedAnnotation(
        val typeIdx: Int,
        val size: Int,
        val elements: Array<AnnotationElement>
) {
    companion object {
        fun create(byteBuffer: ByteBuffer): EncodedAnnotation {
            val typeIdx = Leb128.readUnsignedLeb128(byteBuffer)
            val size = Leb128.readUnsignedLeb128(byteBuffer)
            val elements = Array(size, { index -> AnnotationElement(byteBuffer) })
            return EncodedAnnotation(typeIdx, size, elements)
        }
    }
}