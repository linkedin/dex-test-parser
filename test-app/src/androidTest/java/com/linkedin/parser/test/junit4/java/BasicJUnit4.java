/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.parser.test.junit4.java;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

@TestValueAnnotation(stringValue = "Hello world!")
public class BasicJUnit4 {

    @Test
    @TestValueAnnotation(stringValue = "On a method", intValue = 12345, boolValue = true, longValue = 56789L)
    public void basicJUnit4() {
        assertTrue(true);
    }

    @Test
    @TestValueAnnotation(floatValue = 0.25f, doubleValue = 0.5, byteValue = 0x0f, charValue = '?', shortValue = 3)
    public void basicJUnit4Second() {
        assertTrue(true);
    }
}
