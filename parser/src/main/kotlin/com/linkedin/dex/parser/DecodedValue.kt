package com.linkedin.dex.parser

import com.linkedin.dex.spec.DexFile
import com.linkedin.dex.spec.EncodedValue
import com.linkedin.dex.spec.Leb128

/**
 * A sealed class to represent the decoded values of an EncodedValue object.
 */
// TODO: Add support for complex types
sealed class DecodedValue {
    data class DecodedByte(val value: Byte) : DecodedValue()
    data class DecodedShort(val value: Short) : DecodedValue()
    data class DecodedChar(val value: Char) : DecodedValue()
    data class DecodedInt(val value: Int) : DecodedValue()
    data class DecodedLong(val value: Long) : DecodedValue()
    data class DecodedFloat(val value: Float) : DecodedValue()
    data class DecodedDouble(val value: Double) : DecodedValue()
    // Value is an index into the string table
    data class DecodedString(val value: String) : DecodedValue()

    data class DecodedType(val value: String) : DecodedValue()
    object DecodedNull : DecodedValue()
    data class DecodedBoolean(val value: Boolean) : DecodedValue()
    // TODO: DecodedType
    // TODO: DecodedField
    // TODO: DecodedMethod
    // TODO: DecodedEnum
    // TODO: DecodedArrayValue
    // TODO: DecodedAnnotationValue
    
    companion object {
        /**
         * Resolve an encoded value against the given dexfile
         */
        fun create(dexFile: DexFile, encodedValue: EncodedValue): DecodedValue {
            when (encodedValue) {
                is EncodedValue.EncodedByte -> return DecodedByte(encodedValue.value)
                is EncodedValue.EncodedShort -> return DecodedShort(encodedValue.value)
                is EncodedValue.EncodedChar -> return DecodedChar(encodedValue.value)
                is EncodedValue.EncodedInt -> return DecodedInt(encodedValue.value)
                is EncodedValue.EncodedLong -> return DecodedLong(encodedValue.value)
                is EncodedValue.EncodedFloat -> return DecodedFloat(encodedValue.value)
                is EncodedValue.EncodedDouble -> return DecodedDouble(encodedValue.value)
                is EncodedValue.EncodedString -> {
                    dexFile.byteBuffer.position(dexFile.stringIds[encodedValue.value].stringDataOff)
                    // read past unused size item
                    Leb128.readUnsignedLeb128(dexFile.byteBuffer)
                    return DecodedString(ParseUtils.parseStringBytes(dexFile.byteBuffer))
                }
                is EncodedValue.EncodedType -> {
                    dexFile.byteBuffer.position(dexFile.stringIds[dexFile.typeIds[encodedValue.value].descriptorIdx].stringDataOff)
                    // read past unused size item
                    Leb128.readUnsignedLeb128(dexFile.byteBuffer)
                    return DecodedType(ParseUtils.parseStringBytes(dexFile.byteBuffer))
                }
                is EncodedValue.EncodedBoolean -> return DecodedBoolean(encodedValue.value)
                is EncodedValue.EncodedNull -> return DecodedNull

                else -> return DecodedNull
            }
        }
    }
}