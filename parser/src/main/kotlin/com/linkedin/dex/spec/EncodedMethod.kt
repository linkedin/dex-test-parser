/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.spec

import java.nio.ByteBuffer

data class EncodedMethod(
        val methodIdxDiff: Int,
        val accessFlags: Int,
        val codeOff: Int
) {
    constructor(byteBuffer: ByteBuffer) : this(
            methodIdxDiff = Leb128.readUnsignedLeb128(byteBuffer),
            accessFlags = Leb128.readUnsignedLeb128(byteBuffer),
            codeOff = Leb128.readUnsignedLeb128(byteBuffer)
    )
}