/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.parser.test.junit4.java;

import org.junit.Test;

public interface IgnoreJUnit4TestInterface {

    @Test
    void thisTestShouldNotBeReported();
}
