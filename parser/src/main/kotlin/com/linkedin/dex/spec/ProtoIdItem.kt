/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.spec

import java.nio.ByteBuffer

data class ProtoIdItem(
        val shortyIdx: Int,
        val returnTypeIdx: Int,
        val parametersOff: Int
) {
    companion object {
        val size: Int = 12
    }

    constructor(byteBuffer: ByteBuffer) : this(
            shortyIdx = byteBuffer.int,
            returnTypeIdx = byteBuffer.int,
            parametersOff = byteBuffer.int
    )
}