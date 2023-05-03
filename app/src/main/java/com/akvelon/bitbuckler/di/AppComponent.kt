/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 07 December 2020
 */

package com.akvelon.bitbuckler.di

import android.content.Context

fun appComponent(context: Context) = listOf(
    workerModule,
    navigationModule,
    analyticsModule,
    repositoryModule,
    adsModule(context),
    presenterModule(context),
    networkModule,
    databaseModule,
    prefsModule,
    markwonModule,
    helperModule,
    encryptedPrefModule,
    accountModule
)
