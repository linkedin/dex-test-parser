package com.linkedin.dex

import com.linkedin.dex.parser.DecodedValue
import com.linkedin.dex.parser.DexParser
import com.linkedin.dex.parser.TestMethod
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DexParserShould {
    companion object {
        val APK_PATH = "test-app/build/outputs/apk/androidTest/debug/test-app-debug-androidTest.apk"
    }

    @Test
    fun parseCorrectNumberOfTestMethods() {
        val testMethods = DexParser.findTestNames(APK_PATH, listOf(""))

        assertEquals(21, testMethods.size)
    }

    @Test
    fun parseMethodWithMultipleMethodAnnotations() {
        val testMethods = DexParser.findTestMethods(APK_PATH, listOf("")).filter { it.annotations.filter { it.name.contains("TestValueAnnotation") }.isNotEmpty() }

        assertEquals(4, testMethods.size)

        val method = testMethods[1]
        assertEquals(method.testName, "com.linkedin.parser.test.junit4.java.BasicJUnit4#basicJUnit4")
        // TestValueAnnotation at the class level, Test annotation at the method level, and TestValueAnnotation at the method level
        assertEquals(method.annotations.size, 3)
    }

    @Test
    fun parseMethodWithChildclassAnnotation() {
        val testMethods = DexParser.findTestMethods(APK_PATH, listOf("")).filter { it.annotations.filter { it.name.contains("TestValueAnnotation") }.isNotEmpty() }

        val method = testMethods[0]
        assertEquals("com.linkedin.parser.test.junit4.java.BasicJUnit4#abstractTest", method.testName)
        assertEquals(method.annotations[1].values["stringValue"], DecodedValue.DecodedString("Hello world!"))
    }

    @Test
    fun parseInheritedMethodAnnotation() {
        val testMethods = DexParser.findTestMethods(APK_PATH, listOf("")).filter { it.annotations.filter { it.name.contains("InheritedAnnotation") }.isNotEmpty() }

        val method = testMethods[0]
        assertEquals("com.linkedin.parser.test.junit4.java.BasicJUnit4#concreteTest", method.testName)
        assertEquals(method.annotations[2].values["stringValue"], DecodedValue.DecodedString("Hello world!"))
    }

    @Test
    fun parsNonInheritedMethodAnnotation() {
        val testMethods = DexParser.findTestMethods(APK_PATH, listOf("")).filter { it.annotations.filter { it.name.contains("InheritedAnnotation") }.isNotEmpty() }

        val method = testMethods[0]
        assertEquals("com.linkedin.parser.test.junit4.java.BasicJUnit4#concreteTest", method.testName)
        assertFalse(method.annotations.any { it.name.contains("NonInheritedAnnotation") })
    }

    @Test
    fun parseInheritedClassAnnotation() {
        val testMethods = DexParser.findTestMethods(APK_PATH, listOf("")).filter { it.annotations.filter { it.name.contains("InheritedAnnotation") }.isNotEmpty() }

        val method = testMethods[0]
        assertEquals("com.linkedin.parser.test.junit4.java.BasicJUnit4#concreteTest", method.testName)
        assertTrue(method.annotations.any { it.name == "com.linkedin.parser.test.junit4.java.InheritedClassAnnotation" })
    }

    @Test
    fun parseStringAnnotationValues() {
        val method = getBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("TestValueAnnotation") }

        val classAnnotation = valueAnnotations.first()
        val stringValue = classAnnotation.values["stringValue"]
        assertNotNull(stringValue)
        assertMatches(stringValue, "Hello world!")

        val methodAnnotation = valueAnnotations[1]
        val methodStringValue = methodAnnotation.values["stringValue"]
        assertMatches(methodStringValue, "On a method")
    }

    @Test
    fun parseIntAnnotationValues() {
        val method = getBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("TestValueAnnotation") }

        val methodAnnotation = valueAnnotations[1]
        val value = methodAnnotation.values["intValue"]
        assertMatches(value, 12345)
    }

    @Test
    fun parseBoolAnnotationValues() {
        val method = getBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("TestValueAnnotation") }

        val methodAnnotation = valueAnnotations[1]
        val value = methodAnnotation.values["boolValue"]
        assertMatches(value, true)
    }

    @Test
    fun parseLongAnnotationValues() {
        val method = getBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("TestValueAnnotation") }

        val methodAnnotation = valueAnnotations[1]
        val value = methodAnnotation.values["longValue"]
        assertMatches(value, 56789L)
    }

    @Test
    fun parseFloatAnnotationValues() {
        val method = getSecondBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("TestValueAnnotation") }

        val methodAnnotation = valueAnnotations[1]
        val value = methodAnnotation.values["floatValue"]
        assertMatches(value, 0.25f)
    }

    @Test
    fun parseDoubleAnnotationValues() {
        val method = getSecondBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("TestValueAnnotation") }

        val methodAnnotation = valueAnnotations[1]
        val value = methodAnnotation.values["doubleValue"]
        assertMatches(value, 0.5)
    }

    @Test
    fun parseByteAnnotationValues() {
        val method = getSecondBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("TestValueAnnotation") }

        val methodAnnotation = valueAnnotations[1]
        val value = methodAnnotation.values["byteValue"]
        assertMatches(value, 0x0f.toByte())
    }

    @Test
    fun parseFloatMaxValuesInDoubleFields() {
        val method = getSecondBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("FloatRange") }

        val methodAnnotation = valueAnnotations[0]
        val from = methodAnnotation.values["from"]
        assertMatches(from, 0f.toDouble())
        val to = methodAnnotation.values["to"]
        assertMatches(to, Float.MAX_VALUE.toDouble())
    }

    @Test
    fun parseCharAnnotationValues() {
        val method = getSecondBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("TestValueAnnotation") }

        val methodAnnotation = valueAnnotations[1]
        val value = methodAnnotation.values["charValue"]
        assertMatches(value, '?')
    }

    @Test
    fun parseShortAnnotationValues() {
        val method = getSecondBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("TestValueAnnotation") }

        val methodAnnotation = valueAnnotations[1]
        val value = methodAnnotation.values["shortValue"]
        assertMatches(value, 3.toShort())
    }

    @Test
    fun parseClassArrayAnnotationnValues() {
        val method = getSecondBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("TestValueAnnotation") }

        val methodAnnotation = valueAnnotations[1]
        val value = methodAnnotation.values["arrayTypeValue"]

        // We have to use a string as opposed to a class reference in this assertion, since the way
        // that its actually stored on disk is their special class format and not what class.name
        // will give
        assertMatches(value, arrayOf("Ljava/util/function/Function;", "Ljava/lang/Integer;"))
    }

    @Test
    fun parseMultipleValuesInASingleAnnotation() {
        val method = getBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("TestValueAnnotation") }

        val methodAnnotation = valueAnnotations[1]
        assertMatches(methodAnnotation.values["stringValue"], "On a method")
        assertMatches(methodAnnotation.values["intValue"], 12345)
        assertMatches(methodAnnotation.values["boolValue"], true)
        assertMatches(methodAnnotation.values["longValue"], 56789L)
    }

    @Test
    fun parseEnumAnnotation() {
        val method = getSecondBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("TestValueAnnotation") }

        val methodAnnotation = valueAnnotations[1]
        assertMatches(methodAnnotation.values["enumValue"], "FAIL")
    }

    @Test
    fun parseTypeAnnotation() {
        val method = getSecondBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("TestValueAnnotation") }

        val methodAnnotation = valueAnnotations[1]
        assertMatches(methodAnnotation.values["typeValue"], "Lorg/junit/Test;")
    }

    private fun getBasicJunit4TestMethod(): TestMethod {
        val testMethods = DexParser.findTestMethods(APK_PATH, listOf("")).filter { it.annotations.filter { it.name.contains("TestValueAnnotation") }.isNotEmpty() }.filter { it.testName.equals("com.linkedin.parser.test.junit4.java.BasicJUnit4#basicJUnit4") }

        assertEquals(1, testMethods.size)

        val method = testMethods.first()
        assertEquals(method.testName, "com.linkedin.parser.test.junit4.java.BasicJUnit4#basicJUnit4")

        return method
    }

    private fun getSecondBasicJunit4TestMethod(): TestMethod {
        val testMethods = DexParser.findTestMethods(APK_PATH, listOf("")).filter { it.annotations.filter { it.name.contains("TestValueAnnotation") }.isNotEmpty() }.filter { it.testName.equals("com.linkedin.parser.test.junit4.java.BasicJUnit4#basicJUnit4Second") }

        assertEquals(1, testMethods.size)

        val method = testMethods.first()
        assertEquals(method.testName, "com.linkedin.parser.test.junit4.java.BasicJUnit4#basicJUnit4Second")

        return method
    }

    // region value type matchers
    private fun assertMatches(value: DecodedValue?, string: String) {
        if (value is DecodedValue.DecodedString) {
            assertEquals(string, value.value)
        } else if (value is DecodedValue.DecodedEnum) {
            assertEquals(string, value.value)
        } else if (value is DecodedValue.DecodedType) {
            assertEquals(string, value.value)
        } else {
            throw Exception("Value was not a string type")
        }
    }

    private fun assertMatches(value: DecodedValue?, number: Int) {
        if (value is DecodedValue.DecodedInt) {
            assertEquals(number, value.value)
        } else {
            throw Exception("Value was not an int type")
        }
    }

    private fun assertMatches(value: DecodedValue?, bool: Boolean) {
        if (value is DecodedValue.DecodedBoolean) {
            assertEquals(bool, value.value)
        } else {
            throw Exception("Value was not a boolean type")
        }
    }

    private fun assertMatches(value: DecodedValue?, long: Long) {
        if (value is DecodedValue.DecodedLong) {
            assertEquals(long, value.value)
        } else {
            throw Exception("Value was not a long type")
        }
    }

    private fun assertMatches(value: DecodedValue?, float: Float) {
        if (value is DecodedValue.DecodedFloat) {
            assertEquals(float, value.value)
        } else {
            throw Exception("Value was not a float type")
        }
    }

    private fun assertMatches(value: DecodedValue?, double: Double) {
        if (value is DecodedValue.DecodedDouble) {
            assertEquals(double, value.value, 0.0)
        } else {
            throw Exception("Value was not a double type")
        }
    }

    private fun assertMatches(value: DecodedValue?, byte: Byte) {
        if (value is DecodedValue.DecodedByte) {
            assertEquals(byte, value.value)
        } else {
            throw Exception("Value was not a byte type")
        }
    }

    private fun assertMatches(value: DecodedValue?, char: Char) {
        if (value is DecodedValue.DecodedChar) {
            assertEquals(char, value.value)
        } else {
            throw Exception("Value was not a char type")
        }
    }

    private fun assertMatches(value: DecodedValue?, short: Short) {
        if (value is DecodedValue.DecodedShort) {
            assertEquals(short, value.value)
        } else {
            throw Exception("Value was not a short type")
        }
    }

    private fun assertMatches(value: DecodedValue?, values: Array<String>) {
        if (value is DecodedValue.DecodedArrayValue) {
            val stringValues = value.values.map { (it as? DecodedValue.DecodedType)?.value }.toTypedArray()
            assertArrayEquals(stringValues, values)
        } else {
            throw Exception("Value was not an array value")
        }
    }

    // endregion
}
