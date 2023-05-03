package com.akvelon.bitbuckler.ui.view

import android.content.res.Resources
import android.util.TypedValue

fun Int.dpToPixel(resources: Resources): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        resources.displayMetrics
    ).toInt()
}