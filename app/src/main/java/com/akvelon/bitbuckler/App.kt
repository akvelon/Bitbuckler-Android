/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 07 November 2020
 */

package com.akvelon.bitbuckler

import android.app.Application
import com.akvelon.bitbuckler.di.appComponent
import com.akvelon.bitbuckler.model.repository.SettingsRepository
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    private val settingsRepository: SettingsRepository by inject()

    override fun onCreate() {
        super.onCreate()

        initDI()

        settingsRepository.increaseAppRunCount()

        Thread.currentThread().setUncaughtExceptionHandler { t, e ->
            e.printStackTrace()
        }

        enableCoroutineLogsOnDebugBuild()
    }

    private fun initDI() {
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            workManagerFactory()
            modules(appComponent(applicationContext))
        }
    }

    private fun enableCoroutineLogsOnDebugBuild() {
        if (BuildConfig.DEBUG) {
            System.setProperty(
                kotlinx.coroutines.DEBUG_PROPERTY_NAME,
                kotlinx.coroutines.DEBUG_PROPERTY_VALUE_ON
            )
        }
    }

    companion object {
        var isTrackReposShown = false
    }
}
