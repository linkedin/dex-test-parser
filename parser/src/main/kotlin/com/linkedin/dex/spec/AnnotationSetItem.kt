/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.spec

import java.nio.ByteBuffer

data class AnnotationSetItem(
        val size: Int,
        val entries: Array<AnnotationOffItem>
) {
    companion object {
        fun create(byteBuffer: ByteBuffer, offset: Int): AnnotationSetItem {
            byteBuffer.position(offset)

            val size = byteBuffer.int
            val entries = Array(size, { index -> AnnotationOffItem(byteBuffer) })
            return AnnotationSetItem(size, entries)
        }
    }
}