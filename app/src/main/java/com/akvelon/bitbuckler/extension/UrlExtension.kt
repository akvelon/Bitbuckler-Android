package com.akvelon.bitbuckler.extension

val regex = "([A-Za-z]*:\\/\\/)?\\S*".toRegex()

fun String.isValidUrl(): Boolean {
    return regex.containsMatchIn(this) && startsWith("https://")
}