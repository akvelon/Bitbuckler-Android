/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 22 December 2020
 */

package com.akvelon.bitbuckler.extension

import java.util.*

inline fun String.ifIsNotNullOrEmpty(action: (String) -> Unit) {
    if (!isNullOrEmpty()) action(this)
}

fun String.dropFirstSymbol() = if (isNotEmpty()) removeRange(0, 1) else this

fun String.startsWithOneOf(prefixes: List<CharSequence>): Boolean {
    prefixes.forEach { prefix ->
        if (this.startsWith(prefix)) return true
    }

    return false
}

fun String.upperFirst(locale: Locale) =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }

fun String.capitalizeFirstThreeChars() =
    this.lowercase().replaceFirstChar(Char::titlecase).subSequence(0, 3)

fun String.toMap() =
    substring(1, length - 1).split(", ").associate {
        val (left, right) = it.split("=")
        left to right
    }

val String.capitalized get() = lowercase().replaceFirstChar { it.uppercase() }