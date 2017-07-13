/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
@file:Suppress("DEPRECATION")

package com.linkedin.parser.test.junit3.kotlin

import android.app.Application
import android.support.test.filters.MediumTest
import android.test.ApplicationTestCase

class KotlinJUnit3WithAnnotations : ApplicationTestCase<Application>(null) {

    @MediumTest
    fun testKotlinJUnit3WithAnnotations() {
        assertTrue(true)
    }
}
