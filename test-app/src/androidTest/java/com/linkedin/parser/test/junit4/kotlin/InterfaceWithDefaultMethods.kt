package com.linkedin.parser.test.junit4.kotlin

import org.junit.Test

interface InterfaceWithDefaultMethods {
    @Test
    fun testMethodShouldNotBeReported() {
    }

    @Test
    fun testToBeOverrideShouldNotBeReportedInInterface() {
    }
}