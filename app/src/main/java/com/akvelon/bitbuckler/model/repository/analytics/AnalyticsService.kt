package com.akvelon.bitbuckler.model.repository.analytics

import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

interface AnalyticsService {
    fun log(event: AnalyticsEvent)
}

class FirebaseAnalyticsService: AnalyticsService {
    private val instance by lazy { Firebase.analytics }
    override fun log(event: AnalyticsEvent) {
        instance.logEvent(event.eventName, event.parameters.toBundle())
    }
}