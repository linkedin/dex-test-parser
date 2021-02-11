/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.parser.test.junit4.java;

import org.junit.Test;

import java.util.function.Function;

import static org.junit.Assert.assertTrue;

@TestValueAnnotation(stringValue = "Hello world!")
public class BasicJUnit4 extends ConcreteTest {

    @Test
    @TestValueAnnotation(stringValue = "On a method", intValue = 12345, boolValue = true, longValue = 56789L, enumValue = TestEnum.SUCCESS)
    public void basicJUnit4() {
        assertTrue(true);
    }

    @Test
    @TestValueAnnotation(floatValue = 0.25f, doubleValue = 0.5, byteValue = 0x0f, charValue = '?', shortValue = 3, enumValue = TestEnum.FAIL, typeValue = Test.class, arrayTypeValue = { Function.class, Integer.class})
    @FloatRange(from = 0f, to = Float.MAX_VALUE)
    public void basicJUnit4Second() {
        assertTrue(true);
    }

    @Test
    private void privateTestShouldNotBeReported() {
        assertTrue(true);
    }

    @NonInheritedAnnotation
    public void customAnnotationTest() {

    }
}
