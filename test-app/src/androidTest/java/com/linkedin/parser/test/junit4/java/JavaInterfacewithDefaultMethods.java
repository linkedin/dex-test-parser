package com.linkedin.parser.test.junit4.java;

import org.junit.Test;

public interface JavaInterfacewithDefaultMethods {

    @Test
    default void overriddenTest() {
    }

    @Test
    default void notOverriddenTest() {
    }
}
