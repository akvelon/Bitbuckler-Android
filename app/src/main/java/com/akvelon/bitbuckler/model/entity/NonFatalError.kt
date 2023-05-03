/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 07 June 2021
 */

package com.akvelon.bitbuckler.model.entity

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

object NonFatalError {
    fun log(error: Throwable, screenName: String) =
        FirebaseCrashlytics.getInstance().apply {
            recordException(error)
            setCustomKey(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        }
}
