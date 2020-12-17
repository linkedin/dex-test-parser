package com.linkedin.dex

import com.linkedin.dex.parser.DexParser
import org.junit.Assert.assertEquals
import org.junit.Test

class AbstractClassInSecondDex {

    companion object {
        /**
         * This apk contains two dex files:
         *
         * classes.dex:
         *
         *      class ConcreteTest : AbstractTest {
         *          @Test fun concreteTest() { ... }
         *      }
         *
         * classes2.dex:
         *
         *      abstract class AbstractTest {
         *          @Test fun abstractTest() { ... }
         *      }
         *
         * The archive was created in a way that "classes.dex" goes before "classes2.dex"
         */
        const val APK_PATH = "parser/src/test/fixtures/abstract-class-in-second-dex.apk"
    }

    @Test
    fun parseMethodFromBaseAbstractClass_whenAbstractClassInTheSecondDex() {
        val testMethods = DexParser
            .findTestMethods(APK_PATH, listOf(""))
            .filter { it.testName == "com.linkedin.parser.test.junit4.java.BasicJUnit4#abstractTest" }

        assertEquals(1, testMethods.size)
    }

    @Test
    fun parseMethodFromConcreteClassThatExtendsFromAbstract_whenAbstractClassInTheSecondDex() {
        val testMethods = DexParser
            .findTestMethods(APK_PATH, listOf(""))
            .filter { it.testName == "com.linkedin.parser.test.junit4.java.BasicJUnit4#concreteTest" }

        assertEquals(1, testMethods.size)
    }
}
