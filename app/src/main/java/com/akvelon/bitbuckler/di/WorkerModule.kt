package com.akvelon.bitbuckler.di

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.WorkManager
import com.akvelon.bitbuckler.domain.repo.*
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val workerModule = module {

    single {
        GetAllUserIssuesWithCommentsUseCase(get(), get(), get(), get())
    }

    single {
        GetAllUserPRsUseCase(get(), get())
    }

    single {
        GetAllTrackedIssuesUseCases(get(), get(), get(), get())
    }

    single {
        GetAllTrackedPRsUseCase(get(), get(), get(), get())
    }

    worker {
        SyncIssuesWorker(get(), get(), get(), get(), get(), get(), get())
    }

    worker {
        SyncPRsWorker(get(), get(), get(), get(), get(), get(), get())
    }

    single {
        Constraints
            .Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    }

    single {
        WorkManager.getInstance(androidApplication())
    }

}