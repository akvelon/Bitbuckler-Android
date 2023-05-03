/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 16 July 2021
 */

package com.akvelon.bitbuckler.extension

import android.util.TypedValue
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.akvelon.bitbuckler.R

fun SwipeRefreshLayout.setPrimaryColorScheme() {
    val primaryColor = TypedValue()
    context.theme.resolveAttribute(R.attr.colorPrimary, primaryColor, true)
    setColorSchemeColors(primaryColor.data)
}
