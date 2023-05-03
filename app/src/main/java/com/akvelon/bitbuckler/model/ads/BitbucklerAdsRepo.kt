package com.akvelon.bitbuckler.model.ads

import android.content.Context
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine

private const val AD_MOB = "AD_MOB"

class BitbucklerAdsRepo(
    private val context: Context
): AdsRepository {

    private val ads = mutableMapOf(AD_MOB to false)

    @ExperimentalCoroutinesApi
    override suspend fun init() {
        suspendCancellableCoroutine<Unit> { continuation ->
            ads.keys.forEach { adName ->
                when(adName) {
                    AD_MOB -> {
                        MobileAds.initialize(context) {
                            ads[adName] = true
                            continuation.resume(Unit) {  }
                        }
                    }
                }
            }
        }
    }
}