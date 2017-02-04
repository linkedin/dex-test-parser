/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.spec

import java.nio.ByteBuffer

data class ClassDataItem(
        val staticFieldsSize: Int,
        val instanceFieldsSize: Int,
        val directMethodsSize: Int,
        val virtualMethodsSize: Int,
        val staticFields: Array<EncodedField>,
        val instanceFields: Array<EncodedField>,
        val directMethods: Array<EncodedMethod>,
        val virtualMethods: Array<EncodedMethod>
) {
    companion object {
        fun create(byteBuffer: ByteBuffer, offset: Int): ClassDataItem {
            byteBuffer.position(offset)

            val staticFieldsSize = Leb128.readUnsignedLeb128(byteBuffer)
            val instanceFieldsSize = Leb128.readUnsignedLeb128(byteBuffer)
            val directMethodsSize = Leb128.readUnsignedLeb128(byteBuffer)
            val virtualMethodsSize = Leb128.readUnsignedLeb128(byteBuffer)
            val staticFields = Array(staticFieldsSize, { index -> EncodedField(byteBuffer) })
            val instanceFields = Array(instanceFieldsSize, { index -> EncodedField(byteBuffer) })
            val directMethods = Array(directMethodsSize, { index -> EncodedMethod(byteBuffer) })
            val virtualMethods = Array(virtualMethodsSize, { index -> EncodedMethod(byteBuffer) })

            return ClassDataItem(
                    staticFieldsSize,
                    instanceFieldsSize,
                    directMethodsSize,
                    virtualMethodsSize,
                    staticFields,
                    instanceFields,
                    directMethods,
                    virtualMethods
            )
        }
    }
}

