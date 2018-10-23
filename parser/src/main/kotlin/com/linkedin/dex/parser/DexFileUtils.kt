package com.linkedin.dex.parser

import com.linkedin.dex.spec.ClassDataItem
import com.linkedin.dex.spec.ClassDefItem
import com.linkedin.dex.spec.DexFile

fun DexFile.findMethodIdxs(classDefItem: ClassDefItem): List<Int> {
    // We need to catch the classes have an offset of 0, and so have no data in this apk to read
    // From the docs: "0 if there is no class data for this class. (This may be the case, for example,
    // if this class is a marker interface.)"
    if (classDefItem.classDataOff == 0) {
        return emptyList()
    }

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