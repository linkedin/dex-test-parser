/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.spec

import java.nio.ByteBuffer

data class TypeIdItem(
        val descriptorIdx: Int
) {
    companion object {
        val size: Int = 4
    }

    constructor(byteBuffer: ByteBuffer) : this(
            descriptorIdx = byteBuffer.int
    )
}