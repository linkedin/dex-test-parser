/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.spec

import java.nio.ByteBuffer

data class ClassDefItem(
        val classIdx: Int,
        val accessFlags: Int,
        val superclassIdx: Int,
        val interfacesOff: Int,
        val sourceFileIdx: Int,
        val annotationsOff: Int,
        val classDataOff: Int,
        val staticValuesOff: Int
) {
    companion object {
        val size: Int = 0x20
    }

    constructor(byteBuffer: ByteBuffer) : this(
            classIdx = byteBuffer.int,
            accessFlags = byteBuffer.int,
            superclassIdx = byteBuffer.int,
            interfacesOff = byteBuffer.int,
            sourceFileIdx = byteBuffer.int,
            annotationsOff = byteBuffer.int,
            classDataOff = byteBuffer.int,
            staticValuesOff = byteBuffer.int
    )
}