package com.linkedin.parser.test.junit4.java;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class IgnoredTest {

    @Ignore @Test
    public void IgnoredTest() {
        assertTrue(true);
    }
}
