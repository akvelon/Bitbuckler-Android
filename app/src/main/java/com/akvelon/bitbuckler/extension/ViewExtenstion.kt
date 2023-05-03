/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 07 November 2020
 */

@file:Suppress("TooManyFunctions")

package com.akvelon.bitbuckler.extension

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.*
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updateLayoutParams
import com.google.android.material.snackbar.Snackbar

private const val SNACKBAR_Z_INDEX = 100.0f

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun View.snack(message: String, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, message, duration).apply {
        view.z = SNACKBAR_Z_INDEX
        show()
    }
}

fun View.snackError(error: Throwable, duration: Int = Snackbar.LENGTH_LONG) {
    snack(getString(error.getHumanReadableMessageRes()), duration)
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.setVisible(show: Boolean) {
    this.visibility = if (show) View.VISIBLE else View.GONE
}

fun View.getString(@StringRes resId: Int): String =
    resources.getString(resId)

fun View.getString(@StringRes resId: Int, vararg formatArgs: Any): String =
    resources.getString(resId, *formatArgs)

fun View.getColor(@ColorRes resId: Int): Int =
    ContextCompat.getColor(this.context, resId)

fun View.setBackgroundTint(@ColorInt color: Int) {
    backgroundTintList = ColorStateList.valueOf(color)
}

fun View.getDimension(@DimenRes resId: Int) =
    resources.getDimension(resId)

fun Group.setAllOnClickListener(listener: View.OnClickListener?) {
    referencedIds.forEach { id -> rootView.findViewById<View>(id).setOnClickListener(listener) }
}

fun View.getDrawable(@DrawableRes resId: Int) = ResourcesCompat.getDrawable(resources, resId, null)

fun View.setWidthToWrapContent() {
    this.updateLayoutParams { width = ViewGroup.LayoutParams.WRAP_CONTENT }
}

fun View.setSafeClickListener(listener: () -> Unit) {
    setOnClickListener {
        isEnabled = false
        listener.invoke()
        postDelayed({ isEnabled = true },800)
    }
}