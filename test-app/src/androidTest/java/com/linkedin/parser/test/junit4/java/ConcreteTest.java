package com.linkedin.parser.test.junit4.java;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ConcreteTest extends AbstractTest {

    @NonInheritedAnnotation
    @InheritedAnnotation
    @Test
    public void concreteTest() {
        assertTrue(true);
    }
}
