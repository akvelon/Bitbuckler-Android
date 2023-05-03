package com.akvelon.bitbuckler.extension

import com.google.android.material.tabs.TabLayout

fun TabLayout.addSelectionListener(listener: (Int?) -> Unit) {
    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            listener.invoke(tab?.position)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}
        override fun onTabReselected(tab: TabLayout.Tab?) {}
    })
}