package com.akvelon.bitbuckler.model.repository.analytics

interface AnalyticsProvider {
    fun report(event: AnalyticsEvent)
}