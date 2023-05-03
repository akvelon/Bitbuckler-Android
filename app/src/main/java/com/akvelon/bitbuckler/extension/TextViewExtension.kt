/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 14 January 2021
 */

package com.akvelon.bitbuckler.extension

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.widget.TextViewCompat

fun TextView.setTextOrHide(text: CharSequence?) {
    if (text.isNullOrEmpty()) this.hide() else this.text = text
}

fun TextView.setTextOrHide(@StringRes stringId: Int?) {
    if (stringId != null) this.text = getString(stringId) else hide()
}

fun TextView.setItalicTypeface() {
    setTypeface(null, Typeface.ITALIC)
}

fun TextView.setNormalTypeface() {
    setTypeface(null, Typeface.NORMAL)
}

fun TextView.setDrawableTop(@DrawableRes resId: Int) {
    setCompoundDrawablesWithIntrinsicBounds(
        null,
        getDrawable(resId),
        null,
        null
    )
}

fun TextView.setDrawableTint(@ColorInt color: Int) {
    TextViewCompat.setCompoundDrawableTintList(this, ColorStateList.valueOf(color))
}
