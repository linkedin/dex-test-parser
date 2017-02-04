/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.spec

import com.linkedin.dex.parser.ParseUtils
import java.nio.ByteBuffer

data class DexMagic(
        val dex: List<Byte>,
        val newline: Byte,
        val version: List<Byte>,
        val zero: Byte
) {
    constructor(byteBuffer: ByteBuffer) : this(
            dex = ParseUtils.parseByteList(byteBuffer, 3),
            newline = byteBuffer.get(),
            version = ParseUtils.parseByteList(byteBuffer, 3),
            zero = byteBuffer.get()
    )

    fun validate() {
        val expectedMagic = DexMagic(
                dex = listOf(0x64, 0x65, 0x78),
                newline = 0xA,
                version = listOf(0x30, 0x33, 0x35),
                zero = 0x00
        )
        if (this != expectedMagic) {
            throw DexException("Invalid dexMagic:\n" + this + "\n" + expectedMagic)
        }
    }
}