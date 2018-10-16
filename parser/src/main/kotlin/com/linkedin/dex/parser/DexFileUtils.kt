package com.linkedin.dex.parser

import com.linkedin.dex.spec.ClassDataItem
import com.linkedin.dex.spec.ClassDefItem
import com.linkedin.dex.spec.DexFile

fun DexFile.findMethodIdxs(classDefItem: ClassDefItem): List<Int> {
    val methodIds = mutableListOf<Int>()
    val testClassData = ClassDataItem.create(byteBuffer, classDefItem.classDataOff)
    var previousMethodIdxOff = 0
    testClassData.virtualMethods.forEachIndexed { index, encodedMethod ->
        var methodIdxOff = encodedMethod.methodIdxDiff
        if (index != 0) {
            methodIdxOff += previousMethodIdxOff
        }
        previousMethodIdxOff = methodIdxOff

        methodIds.add(methodIdxOff)
    }
    return methodIds
}