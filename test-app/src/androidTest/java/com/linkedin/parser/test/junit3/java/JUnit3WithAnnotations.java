/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.parser.test.junit3.java;

import android.support.test.filters.SmallTest;
import android.test.ActivityUnitTestCase;

public class JUnit3WithAnnotations extends ActivityUnitTestCase {

    public JUnit3WithAnnotations() {
        super(null);
    }

    @SmallTest
    public void testJUnit3WithAnnotations() throws Exception {
        assertTrue(true);
    }
}
