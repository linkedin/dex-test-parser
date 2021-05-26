/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.parser

import com.linkedin.dex.spec.ClassDataItem
import com.linkedin.dex.spec.ClassDefItem
import com.linkedin.dex.spec.DexFile
import com.linkedin.dex.spec.MethodIdItem

/**
 * All of the classes that extend JUnit3's TestCase class and are included in the Android SDK.
 * This set is needed because tests inside the apk we're searching may extend any of these and since
 * these are part of the Android SDK, they won't be included in the apk we're searching (so they're root nodes)
 */
private val defaultDescriptors = setOf(
        "Ljunit/framework/TestCase;",
        "Landroid/test/ActivityInstrumentationTestCase;",
        "Landroid/test/ActivityInstrumentationTestCase2;",
        "Landroid/test/ActivityTestCase;",
        "Landroid/test/ActivityUnitTestCase;",
        "Landroid/test/AndroidTestCase;",
        "Landroid/test/ApplicationTestCase;",
        "Landroid/test/FailedToCreateTests;",
        "Landroid/test/InstrumentationTestCase;",
        "Landroid/test/LoaderTestCase;",
        "Landroid/test/ProviderTestCase;",
        "Landroid/test/ProviderTestCase2;",
        "Landroid/test/ServiceTestCase;",
        "Landroid/test/SingleLaunchActivityTestCase;",
        "Landroid/test/SyncBaseInstrumentation;"
)

/**
 * Recursively search through the list of DexFiles to find all JUnit3 tests
 *
 * This function is O(n!), but in practice this is okay because test apks will have a very small number of DexFiles
 */
fun findJUnit3Tests(dexFiles: List<DexFile>): Set<TestMethod> {
    return findJUnit3Tests(dexFiles, mutableSetOf(), defaultDescriptors.toMutableSet()).first
}

private fun findJUnit3Tests(dexFiles: List<DexFile>, testNames: MutableSet<TestMethod>,
                            descriptors: MutableSet<String>): Pair<MutableSet<TestMethod>, MutableSet<String>> {
    // base case
    if (dexFiles.isEmpty()) {
        return Pair(testNames, descriptors)
    }

    // look through each dex file and find the test names and classes that extend TestCase
    // pass the class list through to the next file in case we find something that extends TestCase
    // in one dex file, and there's something that extends THAT in a later dex file
    dexFiles.forEach { dexFile ->
        val newTestNames = dexFile.findJUnit3Tests(descriptors)
        testNames.addAll(newTestNames)
    }

    // chop off the last dex file, we've found everything in it
    // recursively look through all the other dex files again in case there
    // are more tests found using the data we found in the last dex file
    return findJUnit3Tests(dexFiles.subList(0, dexFiles.lastIndex), testNames, descriptors)
}

private fun DexFile.findJUnit3Tests(descriptors: MutableSet<String>): List<TestMethod> {
    val testClasses = findClassesWithSuperClass(descriptors)
    return createTestMethods(testClasses, { classDef, _ -> findMethodIds(classDef) })
            .filter { it.testName.contains("#test") and !it.testName.contains("#testFoo") }
}

fun DexFile.findMethodIds(classDefItem: ClassDefItem): MutableList<MethodIdItem> {
    return findMethodIdxs(classDefItem).map { this.methodIds[it] }.toMutableList()
}

// From the docs:
// The classes must be ordered such that a given class's superclass and
// implemented interfaces appear in the list earlier than the referring class
private fun DexFile.findClassesWithSuperClass(targetDescriptors: MutableSet<String>): List<ClassDefItem> {
    val matchingClasses: MutableList<ClassDefItem> = mutableListOf()

    classDefs.forEach { classDefItem ->
        if (hasDirectSuperClass(classDefItem, targetDescriptors)) {
            matchingClasses.add(classDefItem)
            targetDescriptors.add(ParseUtils.parseDescriptor(byteBuffer, typeIds[classDefItem.classIdx], stringIds))
        }
    }
    return matchingClasses
}

private fun DexFile.hasDirectSuperClass(classDefItem: ClassDefItem, targetDescriptors: Set<String>): Boolean {
    if (classDefItem.superclassIdx == DexFile.NO_INDEX) {
        return false
    }

    val superType = typeIds[classDefItem.superclassIdx]
    val superDescriptor = ParseUtils.parseDescriptor(byteBuffer, superType, stringIds)
    return targetDescriptors.contains(superDescriptor)
}
