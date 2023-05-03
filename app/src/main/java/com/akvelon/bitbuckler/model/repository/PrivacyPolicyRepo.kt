package com.akvelon.bitbuckler.model.repository

import android.content.SharedPreferences

class PrivacyPolicyRepo(
    private val prefs: SharedPreferences
) {
    
    suspend fun updateUserStatus(flag: Boolean) {
        prefs.edit().putBoolean(PRIVACY_POLICY_ACCEPTED, flag).apply()
    }
    
    suspend fun checkUserStatus() = prefs.getBoolean(PRIVACY_POLICY_ACCEPTED, false)
    
    companion object {
        const val PRIVACY_POLICY_ACCEPTED = "PRIVACY_POLICY_ACCEPTED"
    }
}