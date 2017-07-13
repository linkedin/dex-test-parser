/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.spec

import java.nio.ByteBuffer

class EncodedValue(byteBuffer: ByteBuffer) {
    val VALUE_BYTE: Byte = 0x00
    val VALUE_SHORT: Byte = 0x02
    val VALUE_CHAR: Byte = 0x03
    val VALUE_INT: Byte = 0x04
    val VALUE_LONG: Byte = 0x06
    val VALUE_FLOAT: Byte = 0x10
    val VALUE_DOUBLE: Byte = 0x11
    val VALUE_STRING: Byte = 0x17
    val VALUE_TYPE: Byte = 0x18
    val VALUE_FIELD: Byte = 0x19
    val VALUE_METHOD: Byte = 0x1a
    val VALUE_ENUM: Byte = 0x1b
    val VALUE_ARRAY: Byte = 0x1c
    val VALUE_ANNOTATION: Byte = 0x1d
    val VALUE_NULL: Byte = 0x1e
    val VALUE_BOOLEAN: Byte = 0x1f

    // ideally this should have a real type, but the test parser never uses this field so it's not necessary
    val value: Any

    init {
        val argAndType = byteBuffer.get().toInt()

        // first three bits are the optional valueArg
        val valueArg = (argAndType ushr 5).toByte()
        // last five bits are the valueType
        val valueType = (argAndType and 0x1F).toByte()

        when (valueType) {
            VALUE_BYTE -> value = byteArrayOf(byteBuffer.get())
            VALUE_SHORT -> value = ByteArray(sizeOf(valueArg), { byteBuffer.get() })
            VALUE_CHAR -> value = ByteArray(sizeOf(valueArg), { byteBuffer.get() })
            VALUE_INT -> value = ByteArray(sizeOf(valueArg), { byteBuffer.get() })
            VALUE_LONG -> value = ByteArray(sizeOf(valueArg), { byteBuffer.get() })
            VALUE_FLOAT -> value = ByteArray(sizeOf(valueArg), { byteBuffer.get() })
            VALUE_DOUBLE -> value = ByteArray(sizeOf(valueArg), { byteBuffer.get() })
            VALUE_STRING -> value = ByteArray(sizeOf(valueArg), { byteBuffer.get() })
            VALUE_TYPE -> value = ByteArray(sizeOf(valueArg), { byteBuffer.get() })
            VALUE_FIELD -> value = ByteArray(sizeOf(valueArg), { byteBuffer.get() })
            VALUE_METHOD -> value = ByteArray(sizeOf(valueArg), { byteBuffer.get() })
            VALUE_ENUM -> value = ByteArray(sizeOf(valueArg), { byteBuffer.get() })
            VALUE_ARRAY -> value = EncodedArray.create(byteBuffer)
            VALUE_ANNOTATION -> value = EncodedAnnotation.create(byteBuffer)
            VALUE_NULL -> value = byteArrayOf()
            VALUE_BOOLEAN -> value = byteArrayOf(valueArg)
            else -> {
                value = byteArrayOf()
                throw DexException("Bad value type: " + valueType)
            }
        }
    }

    fun sizeOf(valueArg: Byte): Int {
        return valueArg + 1
    }
}
