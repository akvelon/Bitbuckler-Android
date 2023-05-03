package com.akvelon.bitbuckler.di

import android.content.Context
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.domain.ads.AdsUseCase
import com.akvelon.bitbuckler.domain.ads.BitbucklerAds
import com.akvelon.bitbuckler.model.ads.AdsRepository
import com.akvelon.bitbuckler.model.ads.BitbucklerAdsRepo
import com.akvelon.bitbuckler.ui.ads.AdsPresenter
import com.github.terrakok.cicerone.Router
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import org.koin.dsl.module

fun adsModule(context: Context) = module {

    single<AdsUseCase> {
        BitbucklerAds(get(), get(), remoteConfig())
    }

    single<AdsRepository> {
        BitbucklerAdsRepo(context)
    }

    factory { (router: Router) ->
        AdsPresenter(router, get(), get())
    }
}

fun remoteConfig() = Firebase.remoteConfig.apply {
    setConfigSettingsAsync(
        remoteConfigSettings { minimumFetchIntervalInSeconds = 3600 }
    )
    setDefaultsAsync(R.xml.remote_config_defaults)
}