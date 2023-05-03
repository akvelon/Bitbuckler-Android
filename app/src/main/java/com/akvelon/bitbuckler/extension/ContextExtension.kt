/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 18 December 2020
 */

package com.akvelon.bitbuckler.extension

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.content.res.TypedArray
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowInsets
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.akvelon.bitbuckler.R
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@ColorInt
fun Context.color(@ColorRes res: Int): Int {
    return ContextCompat.getColor(this, res)
}

fun Context.nightModeOn(): Boolean {
    val currentMode = resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)
    return currentMode == Configuration.UI_MODE_NIGHT_YES
}

fun Context.getColorFromStyles(resId: Int): Int {
    val typedValue = TypedValue()
    val a: TypedArray =
        this.obtainStyledAttributes(
            typedValue.data,
            intArrayOf(resId)
        )
    val color = a.getColor(0, 0)
    a.recycle()
    return color
}

fun Context.showTrackLimitError(activity: Activity) {
    AlertDialog.Builder(this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Background)
        .setView(activity.layoutInflater.inflate(R.layout.dialog_more_repos, null, false))
        .setPositiveButton(this.getText(R.string.ok_bold)) { dialog, _ ->
            dialog.dismiss()
        }.show()
}

fun getScreenWidth(@NonNull activity: Activity): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = activity.windowManager.currentWindowMetrics
        val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        windowMetrics.bounds.width() - insets.left - insets.right
    } else {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.widthPixels
    }
}

val Throwable.noNetworkConnection: Boolean
    get() {
        return this is UnknownHostException || this is SocketTimeoutException
    }