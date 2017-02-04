/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.spec

import com.linkedin.dex.parser.ParseUtils
import java.nio.ByteBuffer

data class HeaderItem(
        val magic: DexMagic,
        val checksum: Int,
        val signature: List<Byte>,
        val fileSize: Int,
        val headerSize: Int,
        val endianTag: Int,
        val linkSize: Int,
        val linkOff: Int,
        val mapOff: Int,
        val stringIdsSize: Int,
        val stringIdsOff: Int,
        val typeIdsSize: Int,
        val typeIdsOff: Int,
        val protoIdsSize: Int,
        val protoIdsOff: Int,
        val fieldIdsSize: Int,
        val fieldIdsOff: Int,
        val methodIdsSize: Int,
        val methodIdsOff: Int,
        val classDefsSize: Int,
        val classDefsOff: Int,
        val dataSize: Int,
        val dataOff: Int
) {
    constructor(byteBuffer: ByteBuffer) : this(
            magic = DexMagic(byteBuffer),
            checksum = byteBuffer.int,
            signature = ParseUtils.parseByteList(byteBuffer, 20),
            fileSize = byteBuffer.int,
            headerSize = byteBuffer.int,
            endianTag = byteBuffer.int,
            linkSize = byteBuffer.int,
            linkOff = byteBuffer.int,
            mapOff = byteBuffer.int,
            stringIdsSize = byteBuffer.int,
            stringIdsOff = byteBuffer.int,
            typeIdsSize = byteBuffer.int,
            typeIdsOff = byteBuffer.int,
            protoIdsSize = byteBuffer.int,
            protoIdsOff = byteBuffer.int,
            fieldIdsSize = byteBuffer.int,
            fieldIdsOff = byteBuffer.int,
            methodIdsSize = byteBuffer.int,
            methodIdsOff = byteBuffer.int,
            classDefsSize = byteBuffer.int,
            classDefsOff = byteBuffer.int,
            dataSize = byteBuffer.int,
            dataOff = byteBuffer.int
    )

    fun validate() {
        magic.validate()

        val expectedEndianTag = 0x12345678;
        if (endianTag != expectedEndianTag) {
            throw DexException("Invalid endian tag:" + endianTag)
        }
    }
}