package com.linkedin.parser.test.junit4.kotlin

import org.junit.Test

class DefaultInterfaceImplementation : InterfaceWithDefaultMethods {
    @Test
    override fun testToBeOverrideShouldNotBeReportedInInterface() {
        super.testToBeOverrideShouldNotBeReportedInInterface()
    }
}