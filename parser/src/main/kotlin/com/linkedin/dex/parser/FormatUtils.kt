/**
 * Copyright (c) LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.dex.parser

import com.linkedin.dex.spec.ClassDefItem
import com.linkedin.dex.spec.DexFile

/**
 * Format a class descriptor in a human-readable format that is also compatible with Android instrumentation tests
 *
 * For example,
 * "Lorg/junit/Test;"
 * would become:
 * "org.junit.Test"
 */
fun formatDescriptor(descriptor: String): String {
    return descriptor
            // strip off the "L" prefix
            .substring(1)
            // swap out slashes for periods
            .replace('/', '.')
            // strip off the ";"
            .dropLast(1)
}

/**
 * Extract the fully qualified class name from a [ClassDefItem] and format it for use with Android instrumentation tests
 *
 * @see [formatDescriptor]
 */
fun DexFile.formatClassName(classDefItem: ClassDefItem): String {
    val classDescriptor = ParseUtils.parseClassName(byteBuffer, classDefItem, typeIds, stringIds)
    // the instrument command expects test class names and method names to be separated by a "#"
    return formatDescriptor(classDescriptor) + "#"
}
