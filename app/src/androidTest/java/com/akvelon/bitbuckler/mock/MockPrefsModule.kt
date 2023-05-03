/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 07 June 2021
 */

package com.akvelon.bitbuckler.mock

import android.content.Context
import android.content.SharedPreferences
import androidx.test.platform.app.InstrumentationRegistry
import com.akvelon.bitbuckler.PrefsHelper
import com.akvelon.bitbuckler.di.PREFERENCES_NAME
import com.akvelon.bitbuckler.model.datasource.local.Prefs
import org.koin.dsl.module

val mockPrefsModule = module(override = true) {
    single {
        PrefsHelper(get(), get())
    }

    single {
        Prefs(get(), get())
    }

    single {
        sharedPreferences()
    }
}

fun sharedPreferences(): SharedPreferences =
    InstrumentationRegistry
        .getInstrumentation()
        .targetContext
        .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
