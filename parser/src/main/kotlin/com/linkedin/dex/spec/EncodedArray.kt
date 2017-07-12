/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.spec

import java.nio.ByteBuffer

data class EncodedArray(
        val size: Int,
        val values: Array<EncodedValue>
) {
    companion object {
        fun create(byteBuffer: ByteBuffer): EncodedArray {
            val size = Leb128.readUnsignedLeb128(byteBuffer)
            val values = Array(size, { EncodedValue(byteBuffer) })
            return EncodedArray(size, values)
        }
    }
}
