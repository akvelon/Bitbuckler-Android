package com.akvelon.bitbuckler.di

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.akvelon.bitbuckler.model.datasource.local.EncryptedPrefs
import org.koin.dsl.module

const val ENCRYPTED_PREFERENCES_NAME = "app_encrypted_preferences"

val encryptedPrefModule = module {
    single {
        EncryptedPrefs(get(), get(), get())
    }

    single {
        encryptedSharedPreference(get())
    }
}

fun encryptedSharedPreference(context: Context): EncryptedSharedPreferences =
    EncryptedSharedPreferences.create(
        context,
        ENCRYPTED_PREFERENCES_NAME,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    ) as EncryptedSharedPreferences
