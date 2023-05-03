/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 07 December 2020
 */

package com.akvelon.bitbuckler.di

import android.app.Application
import androidx.room.Room
import com.akvelon.bitbuckler.model.datasource.database.AppDatabase
import org.koin.dsl.module

private const val DATABASE_NAME = "app.db"

val databaseModule = module {

    single {
        room(get())
    }

    single {
        workspaceDao(get())
    }

    single {
        trackedPullRequestDao(get())
    }

    single {
        trackedIssuesDao(get())
    }

    single {
        recentRepositoryDao(get())
    }

    single {
        workspaceSizeDao(get())
    }

    single {
        reposLocalStorageDao(get())
    }
}

fun room(app: Application) = Room
    .databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME)
    .build()

fun workspaceDao(db: AppDatabase) = db.workspaceDao()

fun trackedIssuesDao(db: AppDatabase) = db.trackedIssueDao()

fun trackedPullRequestDao(db: AppDatabase) = db.trackedPullRequestDao()

fun recentRepositoryDao(db: AppDatabase) = db.recentRepositoryDao()

fun workspaceSizeDao(db: AppDatabase) = db.workspaceSizeDao()

fun reposLocalStorageDao(db: AppDatabase) = db.reposLocalStorageDao()