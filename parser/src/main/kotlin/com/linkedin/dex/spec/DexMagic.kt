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
        if ((dex[0].toInt() == 0x64) and
                (dex[1].toInt() == 0x65) and
                (dex[2].toInt() == 0x78) and
                (newline.toInt() == 0x0A)  and
                (version[0].toInt() == 0x30) and
                (version[1].toInt() == 0x33) and
                (version[2].toInt() >= 0x35) and
                (zero.toInt() == 0x00)) {
            return
        }
        throw DexException("Invalid dexMagic:\n" + this + "\n")
    }
}