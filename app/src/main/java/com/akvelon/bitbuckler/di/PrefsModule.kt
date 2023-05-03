/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 07 November 2020
 */

package com.akvelon.bitbuckler.di

import android.content.Context
import android.content.SharedPreferences
import com.akvelon.bitbuckler.model.datasource.local.Prefs
import org.koin.dsl.module

const val PREFERENCES_NAME = "app_preferences"

val prefsModule = module {

    single {
        Prefs(get(), get())
    }

    single {
        sharedPreferences(get())
    }
}

fun sharedPreferences(context: Context): SharedPreferences =
    context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
