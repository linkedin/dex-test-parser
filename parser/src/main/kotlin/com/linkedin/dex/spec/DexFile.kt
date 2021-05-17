/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.spec

import com.linkedin.dex.parser.ParseUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder

class DexFile(byteBuffer: ByteBuffer) {
    val byteBuffer: ByteBuffer
    val headerItem: HeaderItem
    val stringIds: Array<StringIdItem>
    val typeIds: Array<TypeIdItem>
    val protoIds: Array<ProtoIdItem>
    val fieldIds: Array<FieldIdItem>
    val methodIds: Array<MethodIdItem>
    val classDefs: Array<ClassDefItem>

    companion object {
        val NO_INDEX = -1
    }

    inline fun <reified T> parse(count: Int, offset: Int, size: Int, init: (ByteBuffer) -> T): Array<T> {
        return Array(count, { index ->
            byteBuffer.position(offset + (index * size))
            init(byteBuffer)
        })
    }

    init {
        this.byteBuffer = byteBuffer.asReadOnlyBuffer().order(ByteOrder.LITTLE_ENDIAN)
        this.byteBuffer.position(0)
        headerItem = HeaderItem(this.byteBuffer)
        headerItem.validate()
        stringIds = parse(headerItem.stringIdsSize, headerItem.stringIdsOff, StringIdItem.size) { StringIdItem(it) }
        typeIds = parse(headerItem.typeIdsSize, headerItem.typeIdsOff, TypeIdItem.size) { TypeIdItem(it) }
        protoIds = parse(headerItem.protoIdsSize, headerItem.protoIdsOff, ProtoIdItem.size) { ProtoIdItem(it) }
        fieldIds = parse(headerItem.fieldIdsSize, headerItem.fieldIdsOff, FieldIdItem.size) { FieldIdItem(it) }
        methodIds = parse(headerItem.methodIdsSize, headerItem.methodIdsOff, MethodIdItem.size) { MethodIdItem(it) }
        classDefs = parse(headerItem.classDefsSize, headerItem.classDefsOff, ClassDefItem.size) { ClassDefItem(it) }
    }

    val inheritedAnnotationTypeIdIndex: Int? by lazy {
        var result: Int? = null
        typeIds.forEachIndexed { index, typeIdItem ->
            if (ParseUtils.parseDescriptor(byteBuffer, typeIdItem, stringIds) == "Ljava/lang/annotation/Inherited;") {
                result = index
            }
        }

        result
    }

    val typeIdToClassDefMap: Map<Int, ClassDefItem> by lazy {
        val map = mutableMapOf<Int, ClassDefItem>()

        for (classDef in classDefs) {
            map[classDef.classIdx] = classDef
        }
        map.toMap()
    }
}
