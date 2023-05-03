package com.akvelon.bitbuckler.domain.ads

import com.akvelon.bitbuckler.model.ads.AdsRepository
import com.akvelon.bitbuckler.model.datasource.local.Prefs
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.IllegalStateException
import kotlin.coroutines.resumeWithException

private const val ADS = "ADS"

class BitbucklerAds(
    private val prefs: Prefs,
    private val repo: AdsRepository,
    private val remoteConfig: FirebaseRemoteConfig
): AdsUseCase {

    override suspend fun initAds() { repo.init() }

    @ExperimentalCoroutinesApi
    override suspend fun areAdsEnabled(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            try {
                remoteConfig
                    .reset()
                    .addOnCompleteListener {
                        remoteConfig.fetchAndActivate().addOnCompleteListener {
                            val flag = remoteConfig.getBoolean(ADS)
                            continuation.resume(flag) { throw IllegalStateException() }
                        }
                    }
            } catch (ex: Exception) {
                continuation.resumeWithException(ex)
            }
        }
    }

    override suspend fun areAdsInitialized() =
        prefs.isMobAdInitialized

    override suspend fun setAdsInitialized(flag: Boolean) {
        prefs.isMobAdInitialized = flag
    }
}

