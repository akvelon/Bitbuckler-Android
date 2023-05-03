package com.akvelon.bitbuckler.ui.view.ads

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.akvelon.bitbuckler.BuildConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

class BitbucklerAdsView(
    private val context: Context,
    private val adType: AdType,
) {

    fun getCurrentAdView(): View {
        return when (adType) {
            is AdType.AdMob -> {
                AdView(context).apply {
                    adUnitId = adType.adId
                    setAdSize(AdSize.BANNER)
                    loadAd(AdRequest.Builder().build())
                }
            }
        }
    }

    companion object {
        val ADMOB = AdType.AdMob(BuildConfig.GOOGLE_ADMOB_ID)
    }
}

fun View.customizeAndAddAdView(root: ViewGroup) {
    apply {
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        layoutParams = params
    }.apply {
        root.addView(this)
    }
}