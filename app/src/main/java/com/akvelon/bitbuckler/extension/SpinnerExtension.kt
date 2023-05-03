/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 18 February 2021
 */

package com.akvelon.bitbuckler.extension

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner

fun Spinner.selected(action: () -> Unit) {
    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            action()
        }
    }
}

fun Spinner.hideSpinnerDropDown() {
    try {
        val method = Spinner::class.java.getDeclaredMethod("onDetachedFromWindow")
        method.isAccessible = true
        method.invoke(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
