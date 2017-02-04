/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.spec

import java.nio.ByteBuffer

data class ParameterAnnotation(
        val parameterIdx: Int,
        val annotationsOff: Int
) {
    companion object {
        val size: Int = 4
    }

    constructor(byteBuffer: ByteBuffer) : this(
            parameterIdx = byteBuffer.int,
            annotationsOff = byteBuffer.int
    )
}