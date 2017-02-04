/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.spec

import java.nio.ByteBuffer

data class FieldIdItem(
        val classIdx: Short,
        val typeIdx: Short,
        val nameIdx: Int
) {
    companion object {
        val size: Int = 8
    }

    constructor(byteBuffer: ByteBuffer) : this(
            classIdx = byteBuffer.short,
            typeIdx = byteBuffer.short,
            nameIdx = byteBuffer.int
    )
}