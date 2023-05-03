/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 10 June 2021
 */

package com.akvelon.bitbuckler.mock

import android.app.Application
import androidx.room.Room
import com.akvelon.bitbuckler.DatabaseHelper
import com.akvelon.bitbuckler.model.datasource.database.AppDatabase
import org.koin.dsl.module

private const val DATABASE_NAME = "test.db"

val mockDatabaseModule = module(override = true) {
    single {
        DatabaseHelper(get(), get())
    }

    single {
        room(get())
    }

    single {
        recentRepositoryDao(get())
    }
}

fun room(app: Application) = Room
    .databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME)
    .build()

fun recentRepositoryDao(db: AppDatabase) = db.recentRepositoryDao()
