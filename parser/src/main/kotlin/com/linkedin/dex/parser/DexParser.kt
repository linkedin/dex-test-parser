/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.parser

import com.linkedin.dex.spec.DexFile
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * Parse an apk file to find any test methods and return the set of fully qualified method names
 *
 * Main entry point to the project.
 * NOTE: everything in the spec package is derived from the dex file format spec:
 * https://source.android.com/devices/tech/dalvik/dex-format.html
 */
class DexParser private constructor() {
    companion object {

        /**
         * Main method included for easy local testing during development
         */
        @JvmStatic
        fun main(vararg args: String) {
            if (args.size != 2) {
                println("Usage: apkPath outputPath")
                System.exit(1)
            }
            val apkPath = args[0]
            val outputPath = args[1]

            val allItems = Companion.findTestNames(apkPath)

            Files.write(File(outputPath + "/AllTests.txt").toPath(), allItems)
        }

        /**
         * Parse the apk found at [apkPath] and return the list of test names found in the apk
         */
        @JvmStatic
        fun findTestNames(apkPath: String): List<String> {
            return findTestMethods(apkPath).map { it.testName }
        }

        /**
         * Parse the apk found at [apkPath] and return a list of [TestMethod] objects containing the test names
         * and their associated annotation names found in the apk. Note that class-level annotations are also
         * included in the list of annotations for a given test and merged with the list of annotations that were
         * explicitly applied to the test method.
         */
        @JvmStatic
        fun findTestMethods(apkPath: String): List<TestMethod> {
            val dexFiles = readDexFiles(apkPath)

            val junit3Items = findJUnit3Tests(dexFiles).sorted()
            val junit4Items = findAllJUnit4Tests(dexFiles).sorted()

            return (junit3Items + junit4Items).sorted()
        }

        fun readDexFiles(path: String): List<DexFile> {
            ZipInputStream(FileInputStream(File(path))).use { zip ->
                return zip.entries()
                        .filter { it.name.endsWith(".dex") }
                        .map { zip.readBytes() }
                        .map { ByteBuffer.wrap(it) }
                        .map(::DexFile)
                        .toList()
            }
        }

        private fun ZipInputStream.entries(): Sequence<ZipEntry> {
            return object : Sequence<ZipEntry> {
                override fun iterator(): Iterator<ZipEntry> {
                    return object : Iterator<ZipEntry> {
                        var hasPeekedNext: Boolean = false
                        var next: ZipEntry? = null

                        override fun hasNext(): Boolean {
                            if (!hasPeekedNext) {
                                next = nextEntry
                                hasPeekedNext = true
                            }
                            return next != null
                        }

                        override fun next(): ZipEntry {
                            hasPeekedNext = false
                            return next!!
                        }
                    }
                }
            }
        }
    }
}
