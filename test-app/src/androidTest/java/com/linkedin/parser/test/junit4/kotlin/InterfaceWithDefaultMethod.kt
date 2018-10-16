package com.linkedin.parser.test.junit4.kotlin

import org.junit.Test

interface InterfaceWithDefaultMethod {
    @Test
    fun testMethodShouldNotBeReported() {
    }
}