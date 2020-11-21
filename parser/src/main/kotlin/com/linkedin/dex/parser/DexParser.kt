/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.parser

import com.linkedin.dex.spec.DexFile
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
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
class DexParserArgs(parser: ArgParser) {
    val apkPath by parser.storing(
        "-a", "--apk-path",
        help = "path to apk file")

    val outputDir by parser.storing(
        "-o",
        "--output-dir",
        help = "path to output dir where AllTests.txt file will be saved, if not set output will go to stdout"
    ).default("")

    val customAnnotations by parser.adding(
        "-A", help = "add custom annotation used by tests") { toString() }
}

 class DexParser private constructor() {
    companion object {

        /**
         * Main method included for easy local testing during development
         */
        @JvmStatic
        fun main(vararg args: String) {
            val parsedArgs = ArgParser(args).parseInto(::DexParserArgs)
            parsedArgs.run {
                val allItems = Companion.findTestNames(apkPath, customAnnotations)
                if (outputDir.isEmpty()) {
                    println(allItems.joinToString(separator = "\n"))
                } else {
                    Files.write(File(outputDir + "/AllTests.txt").toPath(), allItems)
                }
            }
        }

        /**
         * Parse the apk found at [apkPath] and return the list of test names found in the apk
         */
        @JvmStatic
        fun findTestNames(apkPath: String, customAnnotations: List<String>): List<String> {
            return findTestMethods(apkPath, customAnnotations).map { it.testName }
        }

        /**
         * Parse the apk found at [apkPath] and return a list of [TestMethod] objects containing the test names
         * and their associated annotation names found in the apk. Note that class-level annotations are also
         * included in the list of annotations for a given test and merged with the list of annotations that were
         * explicitly applied to the test method.
         */
        @JvmStatic
        fun findTestMethods(apkPath: String, customAnnotations: List<String>): List<TestMethod> {
            val dexFiles = readDexFiles(apkPath)

            val junit3Items = findJUnit3Tests(dexFiles).sorted()
            val junit4Items = findAllJUnit4Tests(dexFiles, customAnnotations).sorted()

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
