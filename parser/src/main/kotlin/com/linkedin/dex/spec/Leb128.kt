/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.spec

import java.nio.ByteBuffer

class Leb128 private constructor() {
    companion object {
        /**
         * Reads an unsigned integer from byteBuffer.
         */
        fun readUnsignedLeb128(byteBuffer: ByteBuffer): Int {
            var result = 0
            var current: Int
            var count = 0

            do {
                current = byteBuffer.get().toInt() and 0xff
                result = result or (current and 0x7f shl count * 7)
                count++
            } while (current and 0x80 == 0x80 && count < 5)

            if (current and 0x80 == 0x80) {
                throw DexException("invalid LEB128 sequence")
            }

            return result
        }
    }
}
