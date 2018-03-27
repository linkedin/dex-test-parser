/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.parser.test.junit4.java;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

@TestValueAnnotation("Hello world!")
public class BasicJUnit4 {

    @Test
    @TestValueAnnotation("On a method")
    public void basicJUnit4() {
        assertTrue(true);
    }
}
