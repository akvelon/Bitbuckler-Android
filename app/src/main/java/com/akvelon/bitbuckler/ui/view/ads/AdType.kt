package com.akvelon.bitbuckler.ui.view.ads

sealed class AdType(val adId: String) {
    class AdMob(adId: String): AdType(adId)
}