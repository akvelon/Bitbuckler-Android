package com.akvelon.bitbuckler.model.repository.analytics

import android.util.Log

class BitbucklerAnalyticsProvider: AnalyticsProvider {

    private var providers: Map<String, AnalyticsService> = mapOf()

    init {
        providers = mapOf(FIREBASE to FirebaseAnalyticsService())
    }

    override fun report(event: AnalyticsEvent) {
        providers.values.forEach { provider ->
            provider.log(event)
        }
        if(event.parameters.isEmpty()) {
            Log.d(ANALYTICS_TAG, "event name = ${event.eventName}")
        } else {
            Log.d(ANALYTICS_TAG, "event name = ${event.eventName}, parameters:")
            event.parameters.forEach { parameter ->
                Log.d(ANALYTICS_TAG, "key = ${parameter.key}, value = ${parameter.value}")
            }
        }
    }

    companion object {
        const val FIREBASE = "FIREBASE"
        const val ANALYTICS_TAG = "Analytics"
    }
}