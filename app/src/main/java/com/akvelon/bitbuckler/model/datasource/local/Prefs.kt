/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 07 November 2020
 */

package com.akvelon.bitbuckler.model.datasource.local

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.akvelon.bitbuckler.extension.fromJson
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequestState
import com.akvelon.bitbuckler.model.entity.repository.issue.IssueFilter
import com.google.gson.Gson

class Prefs(
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson
) {

    var lastPullRequestState: PullRequestState?
        get() = sharedPrefs.getString(KEY_LAST_PULL_REQUEST_STATE, PullRequestState.OPEN.toString())
            ?.let {
                gson.fromJson<PullRequestState>(it)
            }
        set(value) {
            sharedPrefs.edit { putString(KEY_LAST_PULL_REQUEST_STATE, value.toString()) }
        }

    var lastIssueFilter: IssueFilter?
        get() = sharedPrefs.getString(KEY_LAST_ISSUE_FILTER, IssueFilter.ALL.toString())?.let {
            gson.fromJson<IssueFilter>(it)
        }
        set(value) {
            sharedPrefs.edit { putString(KEY_LAST_ISSUE_FILTER, value.toString()) }
        }

    var appRunCount: Int
        get() = sharedPrefs.getInt(KEY_APP_RUN_COUNT, 0)
        set(value) {
            sharedPrefs.edit { putInt(KEY_APP_RUN_COUNT, value) }
        }

    var nightMode: Int
        get() = sharedPrefs.getInt(KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_NO)
        set(value) {
            sharedPrefs.edit { putInt(KEY_NIGHT_MODE, value) }
        }

    var appVersion: String?
        get() = sharedPrefs.getString(KEY_APP_VERSION, null)
        set(value) {
            sharedPrefs.edit { putString(KEY_APP_VERSION, value) }
        }

    var activityFilterState: PullRequestState
        get() = sharedPrefs.getString(KEY_ACTIVITY_FILTER_STATE, PullRequestState.OPEN.toString())
            ?.let {
                gson.fromJson<PullRequestState>(it)
            } ?: PullRequestState.OPEN
        set(value) {
            sharedPrefs.edit { putString(KEY_ACTIVITY_FILTER_STATE, value.toString()) }
        }

    var isMobAdInitialized: Boolean
        get() = sharedPrefs.getBoolean(KEY_AD_MOB, false)
        set(value) {
            sharedPrefs.edit { putBoolean(KEY_AD_MOB, value) }
        }

    fun clear() = sharedPrefs.edit {
        clear()
    }

    var isDonationMade: Boolean
        get() {
            return sharedPrefs.getBoolean(KEY_DONATION_MADE, false)
        }
        set(value) {
            sharedPrefs.edit { putBoolean(KEY_DONATION_MADE, value) }
        }

    var isDonationReminderSet: Boolean
        get() {
            return sharedPrefs.getBoolean(KEY_DONATION_REMINDER_SET, true)
        }
        set(value) {
            sharedPrefs.edit { putBoolean(KEY_DONATION_REMINDER_SET, value) }
        }

    companion object {
        const val KEY_LAST_PULL_REQUEST_STATE = "last_pull_request_state"
        const val KEY_LAST_ISSUE_FILTER = "last_issue_filter"
        const val KEY_APP_RUN_COUNT = "app_run_count"
        const val KEY_NIGHT_MODE = "night_mode"
        const val KEY_APP_VERSION = "app_version"
        const val KEY_AD_MOB = "KEY_AD_MOB"
        const val KEY_ACTIVITY_FILTER_STATE = "activity_filter_state"
        const val KEY_DONATION_MADE = "key_donation_made"
        const val KEY_DONATION_REMINDER_SET = "key_donation_reminder_set"
    }
}
