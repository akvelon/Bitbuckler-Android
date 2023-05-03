package com.akvelon.bitbuckler.di

import com.akvelon.bitbuckler.model.repository.analytics.AnalyticsProvider
import com.akvelon.bitbuckler.model.repository.analytics.BitbucklerAnalyticsProvider
import org.koin.dsl.module

val analyticsModule = module {
    single<AnalyticsProvider> {
        BitbucklerAnalyticsProvider()
    }
}