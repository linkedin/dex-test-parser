/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.parser

import com.linkedin.dex.spec.*
import java.nio.ByteBuffer
import java.util.ArrayList

/**
 * Utility methods for parsing different types of data out of a dex file.
 */
object ParseUtils {
    fun parseClassName(byteBuffer: ByteBuffer, classDefItem: ClassDefItem,
                       typeIds: Array<TypeIdItem>, stringIds: Array<StringIdItem>): String {
        val typeIdItem = typeIds[classDefItem.classIdx]
        return parseDescriptor(byteBuffer, typeIdItem, stringIds)
    }

    fun parseDescriptor(byteBuffer: ByteBuffer, typeIdItem: TypeIdItem, stringIds: Array<StringIdItem>): String {
        val descriptorIdx = typeIdItem.descriptorIdx
        val descriptorId = stringIds[descriptorIdx]
        byteBuffer.position(descriptorId.stringDataOff)
        // read past unused descriptorSize item
        Leb128.readUnsignedLeb128(byteBuffer)
        val encodedDescriptorName = parseStringBytes(byteBuffer)
        return encodedDescriptorName
    }

    fun parseMethodName(byteBuffer: ByteBuffer, stringIds: Array<StringIdItem>, methodId: MethodIdItem): String {
        val methodNameStringId = stringIds[methodId.nameIdx]
        byteBuffer.position(methodNameStringId.stringDataOff)
        // read past unused size item
        Leb128.readUnsignedLeb128(byteBuffer)
        val encodedName = parseStringBytes(byteBuffer)
        return encodedName
    }

    fun parseValueName(byteBuffer: ByteBuffer, stringIds: Array<StringIdItem>, nameIdx: Int): String {
        val stringId = stringIds[nameIdx]
        byteBuffer.position(stringId.stringDataOff)
        // read past unused size item
        Leb128.readUnsignedLeb128(byteBuffer)
        val encodedName = parseStringBytes(byteBuffer)
        return encodedName
    }

    fun parseStringBytes(byteBuffer: ByteBuffer): String {
        val byteList = ArrayList<Byte>()
        do {
            val nextByte = byteBuffer.get()
            byteList.add(nextByte)
        } while (nextByte != 0.toByte())
        byteList.removeAt(byteList.size - 1)

        return String(byteList.toByteArray())
    }

    fun parseByteList(byteBuffer: ByteBuffer, length: Int): List<Byte> {
        val tmpArray = ByteArray(length)
        byteBuffer.get(tmpArray, 0, length)
        return tmpArray.toList()
    }
}