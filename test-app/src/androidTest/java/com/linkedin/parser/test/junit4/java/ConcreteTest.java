package com.linkedin.parser.test.junit4.java;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

@InheritedClassAnnotation
public class ConcreteTest extends AbstractTest {

    @NonInheritedAnnotation
    @InheritedAnnotation
    @Test
    public void concreteTest() {
        assertTrue(true);
    }

    @NonInheritedAnnotation
    @InheritedAnnotation
    @Test
    public void nonOverriddenConcreteTest() {
        assertTrue(true);
    }
}
