/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.parser.test.junit3.java;

import android.test.SingleLaunchActivityTestCase;

public class JUnit3TestInsideStaticInnerClass extends SingleLaunchActivityTestCase {

    public JUnit3TestInsideStaticInnerClass() {
        super(null, null);
    }

    public void testJUnit3TestInsideStaticInnerClass() throws Exception {
        assertTrue(true);
    }
}
