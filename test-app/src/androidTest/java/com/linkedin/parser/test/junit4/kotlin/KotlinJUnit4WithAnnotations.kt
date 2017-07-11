/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.parser.test.junit4.kotlin

import android.support.test.filters.LargeTest
import org.junit.Assert.assertTrue
import org.junit.Test

class KotlinJUnit4WithAnnotations {

    @Test
    @LargeTest
    fun testKotlinJUnit4WithAnnotations() {
        assertTrue(true)
    }
}
