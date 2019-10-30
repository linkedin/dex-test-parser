/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.parser.test.junit4.java;

import android.support.annotation.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.METHOD, ElementType.TYPE })
public @interface TestValueAnnotation {

    @NonNull
    String stringValue() default "";

    int intValue() default 0;

    boolean boolValue() default false;

    long longValue() default 0L;

    float floatValue() default 0f;

    double doubleValue() default 0d;

    byte byteValue() default 0;

    char charValue() default 0;

    short shortValue() default 0;

    TestEnum enumValue() default TestEnum.SUCCESS;

    Class typeValue() default Object.class;

    Class[] arrayTypeValue() default {};
}
