/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.spec

import java.nio.ByteBuffer

data class MethodIdItem(
        val classIdx: Short,
        val protoIdx: Short,
        val nameIdx: Int
) {
    companion object {
        val size: Int = 8
    }

    constructor(byteBuffer: ByteBuffer) : this(
            classIdx = byteBuffer.short,
            protoIdx = byteBuffer.short,
            nameIdx = byteBuffer.int
    )
}