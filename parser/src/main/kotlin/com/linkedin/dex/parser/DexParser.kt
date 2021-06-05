/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.parser

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
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
private class DexParserCommand : CliktCommand() {
    val apkPath: String by argument().help("path to apk file")

    val outputDir by argument().help("path to output dir where AllTests.txt file will be saved, if not set output will go to stdout"
    ).default("")

    val customAnnotations: List<String> by option("-A", "--annotation").multiple().help("add custom annotation used by tests")

    override fun run() {
        val allItems = DexParser.findTestNames(apkPath, customAnnotations)
        if (outputDir.isEmpty()) {
            println(allItems.joinToString(separator = "\n"))
        } else {
            Files.write(File("$outputDir/AllTests.txt").toPath(), allItems)
        }
    }
}

 class DexParser private constructor() {
    companion object {

        /**
         * Main method included for easy local testing during development
         */
        @JvmStatic
        fun main(vararg args: String) {
            DexParserCommand().main(args)
        }

        /**
         * Parse the apk found at [apkPath] and return the list of test names found in the apk
         */
        @JvmStatic
        @JvmOverloads
        fun findTestNames(apkPath: String, customAnnotations: List<String> = emptyList()): List<String> {
            return findTestMethods(apkPath, customAnnotations).map { it.testName }.distinct()
        }

        /**
         * Parse the apk found at [apkPath] and return a list of [TestMethod] objects containing the test names
         * and their associated annotation names found in the apk. Note that class-level annotations are also
         * included in the list of annotations for a given test and merged with the list of annotations that were
         * explicitly applied to the test method.
         */
        @JvmStatic
        @JvmOverloads
        fun findTestMethods(apkPath: String, customAnnotations: List<String> = emptyList()): List<TestMethod> {
            val dexFiles = readDexFiles(apkPath)

            val junit3Items = findJUnit3Tests(dexFiles).sorted()
            val junit4Items = findAllJUnit4Tests(dexFiles, customAnnotations).sorted()

            return (junit3Items + junit4Items).sorted()
        }

        fun readDexFiles(path: String): List<DexFile> {
            ZipInputStream(FileInputStream(File(path))).use { zip ->
                return zip.entries()
                        .filter {
                            it.name.endsWith(".dex") or
                                    (it.name.contains("secondary-program-dex-jars")
                                            and it.name.endsWith(".jar"))
                        }
                        .map {
                            if (it.name.endsWith(".jar")) {
                                readSecondaryDex(zip.readBytes())
                            } else {
                                zip.readBytes()
                            }
                        }
                        .map { ByteBuffer.wrap(it) }
                        .map { DexFile(it) }
                        .toList()
            }
        }

        private fun readSecondaryDex(jardex: ByteArray): ByteArray {
            ZipInputStream(jardex.inputStream()).use { zip ->
                return zip.entries()
                    .filter {
                        it.name.endsWith(".dex")
                    }
                    .map{ zip.readBytes() }
                    .first()
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
