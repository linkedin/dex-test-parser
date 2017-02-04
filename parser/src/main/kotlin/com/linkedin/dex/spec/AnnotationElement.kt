/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.spec

import java.nio.ByteBuffer

data class AnnotationElement(
        val nameIdx: Int,
        val value: EncodedValue
) {
    constructor(byteBuffer: ByteBuffer) : this(
            nameIdx = Leb128.readUnsignedLeb128(byteBuffer),
            value = EncodedValue(byteBuffer)
    )
}