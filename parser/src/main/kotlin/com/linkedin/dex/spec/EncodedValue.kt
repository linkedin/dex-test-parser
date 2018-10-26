/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.spec

import java.nio.ByteBuffer
import java.nio.ByteOrder

sealed class EncodedValue {
    data class EncodedByte(val value: Byte): EncodedValue()
    data class EncodedShort(val value: Short): EncodedValue()
    data class EncodedChar(val value: Char): EncodedValue()
    data class EncodedInt(val value: Int): EncodedValue()
    data class EncodedLong(val value: Long): EncodedValue()
    data class EncodedFloat(val value: Float): EncodedValue()
    data class EncodedDouble(val value: Double): EncodedValue()
    // Value is an index into the string table
    data class EncodedString(val value: Int): EncodedValue()
    data class EncodedType(val value: Int): EncodedValue()
    data class EncodedField(val value: Int): EncodedValue()
    data class EncodedMethod(val value: Int): EncodedValue()
    data class EncodedEnum(val value: Int): EncodedValue()
    data class EncodedArrayValue(val value: EncodedArray): EncodedValue()
    data class EncodedAnnotationValue(val value: EncodedAnnotation): EncodedValue()
    object EncodedNull: EncodedValue()
    data class EncodedBoolean(val value: Boolean): EncodedValue()
    companion object {
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

        fun create(byteBuffer: ByteBuffer): EncodedValue {
            val argAndType = byteBuffer.get().toInt().toUnsigned8BitInt()

            // first three bits are the optional valueArg
            val valueArg = (argAndType ushr 5).toByte()
            // last five bits are the valueType
            val valueType = (argAndType and 0x1F).toByte()

            when (valueType) {
                VALUE_BYTE -> return EncodedByte(byteBuffer.get())
                VALUE_SHORT -> return EncodedShort(getPaddedBuffer(byteBuffer, sizeOf(valueArg), 2).short)
                VALUE_CHAR -> return EncodedChar(getPaddedBuffer(byteBuffer, sizeOf(valueArg), 2).char)
                VALUE_INT -> return EncodedInt(getPaddedBuffer(byteBuffer, sizeOf(valueArg), 4).int)
                VALUE_LONG -> return EncodedLong(getPaddedBuffer(byteBuffer, sizeOf(valueArg), 8).long)
                VALUE_FLOAT -> return EncodedFloat(getPaddedBufferToTheRight(byteBuffer, sizeOf(valueArg), 4).float)
                VALUE_DOUBLE -> return EncodedDouble(getPaddedBufferToTheRight(byteBuffer, sizeOf(valueArg), 8).double)
                VALUE_STRING -> return EncodedString(getPaddedBuffer(byteBuffer, sizeOf(valueArg), 4).int)
                VALUE_TYPE -> return EncodedType(getPaddedBuffer(byteBuffer, sizeOf(valueArg), 4).int)
                VALUE_FIELD -> return EncodedField(getPaddedBuffer(byteBuffer, sizeOf(valueArg), 4).int)
                VALUE_METHOD -> return EncodedMethod(getPaddedBuffer(byteBuffer, sizeOf(valueArg), 4).int)
                VALUE_ENUM -> return EncodedEnum(getPaddedBuffer(byteBuffer, sizeOf(valueArg), 4).int)
                VALUE_ARRAY -> return EncodedArrayValue(EncodedArray.create(byteBuffer))
                VALUE_ANNOTATION -> return EncodedAnnotationValue(EncodedAnnotation.create(byteBuffer))
                VALUE_NULL -> return EncodedNull
                VALUE_BOOLEAN -> return EncodedBoolean(valueArg.toInt() == 1)
                else -> {
                    throw DexException("Bad value type: " + valueType)
                }
            }
        }

        // The size of the field is generaly represented as 1 more than the value of the first byte
        // See https://source.android.com/devices/tech/dalvik/dex-format#encoding
        private fun sizeOf(valueArg: Byte): Int {
            return valueArg + 1
        }

        // In the dex format, when a value can be represented with less than the bytes defined by its type (ex, an Int
        // that can be represented in only 1 byte), then it does not pad the extra bytes
        // ByteBuffer makes parsing bytes nice since it handles endianness and other small issues, so we can just create
        // a buffer and fill in the extra bits not specified in the file to fill the appropriate size for the type
        private fun getPaddedBuffer(byteBuffer: ByteBuffer, size: Int, fullSize: Int): ByteBuffer {
            val buffer = ByteBuffer.allocate(fullSize)
            buffer.order(ByteOrder.LITTLE_ENDIAN)
            var i = 0
            while (i < size) {
                i++
                buffer.put(byteBuffer.get())
            }
            for (x in size+1..fullSize) {
                buffer.put(0)
            }

            // Move to the start of the buffer so we can read values
            buffer.position(0)

            return buffer
        }

        // For float and double values, the value is padded to the right, so we need to build the buffer in the
        // opposite order
        private fun getPaddedBufferToTheRight(byteBuffer: ByteBuffer, size: Int, fullSize: Int): ByteBuffer {
            val buffer = ByteBuffer.allocate(fullSize)
            buffer.order(ByteOrder.LITTLE_ENDIAN)

            for (x in size+1..fullSize) {
                buffer.put(0)
            }

            var i = 0
            while (i < size) {
                i++
                buffer.put(byteBuffer.get())
            }


            // Move to the start of the buffer so we can read values
            buffer.position(0)

            return buffer
        }
    }
}

private fun Int.toUnsigned8BitInt(): Int = (this and 0xFF)
