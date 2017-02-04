/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.spec

import java.nio.ByteBuffer

data class EncodedField(
        val fieldIdxDiff: Int,
        val accessFlags: Int
) {
    constructor(byteBuffer: ByteBuffer) : this(
            fieldIdxDiff = Leb128.readUnsignedLeb128(byteBuffer),
            accessFlags = Leb128.readUnsignedLeb128(byteBuffer)
    )
}