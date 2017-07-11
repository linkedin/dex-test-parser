/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.parser.test.junit3.kotlin

import android.app.Service
import android.test.ServiceTestCase

class KotlinJUnit3TestInsideStaticInnerClass {

    class InnerClass : ServiceTestCase<Service>(null) {

        fun testKotlinJUnit3TestInsideStaticInnerClass() {
            assertTrue(true)
        }
    }
}
