package com.linkedin.dex.parser

/**
 * A class to represent an annotation on method. Includes both the name of the annotation itself,
 * and all of the values within it as a key-value map of name string to value
 */
data class TestAnnotation(val name: String, val values: Map<String, DecodedValue>, val isClassAnnotation: Boolean)
