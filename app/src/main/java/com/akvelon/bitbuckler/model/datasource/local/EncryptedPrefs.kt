package com.akvelon.bitbuckler.model.datasource.local

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import com.akvelon.bitbuckler.extension.fromJson
import com.akvelon.bitbuckler.model.entity.LocalAccount
import com.akvelon.bitbuckler.model.entity.User
import com.google.gson.Gson

class EncryptedPrefs(
    private val encryptedSharedPrefs: EncryptedSharedPreferences,
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson
) {

    var accessToken: String?
        get() = getOrUpdateString(KEY_ACCESS_TOKEN)
        set(value) {
            encryptedSharedPrefs.edit { putString(KEY_ACCESS_TOKEN, value) }
        }

    var refreshToken: String?
        get() = getOrUpdateString(KEY_REFRESH_TOKEN)
        set(value) {
            encryptedSharedPrefs.edit { putString(KEY_REFRESH_TOKEN, value) }
        }

    var currentAccount: User?
        get() {
            encryptedSharedPrefs.getString(KEY_ACCOUNT, null)
                ?.let { return gson.fromJson<User>(it) }

            val current = sharedPrefs.getString(KEY_ACCOUNT, null)?.let { gson.fromJson<User>(it) }
                ?: return null

            encryptedSharedPrefs.edit { putString(KEY_ACCOUNT, gson.toJson(current)) }
            clearSharedPrefs(KEY_ACCOUNT)

            return encryptedSharedPrefs.getString(KEY_ACCOUNT, null)
                ?.let { gson.fromJson<User>(it) }
        }
        set(value) {
            encryptedSharedPrefs.edit { putString(KEY_ACCOUNT, gson.toJson(value)) }
        }

    fun getUserAccounts(): MutableSet<LocalAccount> {
        val accounts = encryptedSharedPrefs.getStringSet(KEY_ACCOUNTS, null) ?: mutableSetOf<String>()
        val users = mutableSetOf<LocalAccount>().apply { accounts.forEach { account -> add(gson.fromJson(account)) } }
        return users
    }

    fun addUserAccount(localAccount: LocalAccount) {
        val mSet = encryptedSharedPrefs.getStringSet(KEY_ACCOUNTS, null) ?: mutableSetOf<String>()
        mSet.add(gson.toJson(localAccount))
        encryptedSharedPrefs.edit().putStringSet(KEY_ACCOUNTS, mSet).apply()
    }

    fun removeAccount(user: User, onResult: () -> Unit) {
        val userAccounts = getUserAccounts()
        userAccounts.remove(userAccounts.find { it.uuid == user.uuid })
        val userAccountsJson = userAccounts.map { gson.toJson(it) }.toSet()
        encryptedSharedPrefs.edit().putStringSet(KEY_ACCOUNTS, userAccountsJson).apply()
        onResult.invoke()
    }

    var authenticatedEmails: Set<String>?
        get() {
            encryptedSharedPrefs.getStringSet(KEY_USER_EMAILS, null)?.let { return it }
            val current = sharedPrefs.getStringSet(KEY_USER_EMAILS, null) ?: return null

            encryptedSharedPrefs.edit { putStringSet(KEY_USER_EMAILS, current) }
            clearSharedPrefs(KEY_USER_EMAILS)

            return encryptedSharedPrefs.getStringSet(KEY_USER_EMAILS, null)
        }
        set(value) {
            encryptedSharedPrefs.edit { putStringSet(KEY_USER_EMAILS, value) }
        }

    fun clear() = encryptedSharedPrefs.edit {
        clear()
    }

    private fun getOrUpdateString(key: String): String? {
        encryptedSharedPrefs.getString(key, null)?.let { return it }
        val current = sharedPrefs.getString(key, null) ?: return null

        encryptedSharedPrefs.edit { putString(key, current) }
        clearSharedPrefs(key)

        return encryptedSharedPrefs.getString(key, null)
    }

    private fun clearSharedPrefs(key: String) {
        sharedPrefs.edit { remove(key) }
    }

    companion object {
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_ACCOUNT = "account"
        const val KEY_ACCOUNTS = "accounts"
        const val KEY_USER_EMAILS = "user_emails"
    }
}
