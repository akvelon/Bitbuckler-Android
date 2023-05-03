/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 07 November 2020
 */

package com.akvelon.bitbuckler.model.repository

import com.akvelon.bitbuckler.BuildConfig
import com.akvelon.bitbuckler.model.datasource.api.bit.BitApi
import com.akvelon.bitbuckler.model.datasource.api.bit.response.PagedResponse
import com.akvelon.bitbuckler.model.datasource.local.EncryptedPrefs
import com.akvelon.bitbuckler.model.datasource.local.Prefs
import com.akvelon.bitbuckler.model.entity.*
import com.akvelon.bitbuckler.model.repository.analytics.models.AnalyticsEventNames
import com.akvelon.bitbuckler.model.repository.analytics.models.AnalyticsParameterName
import com.akvelon.bitbuckler.model.repository.analytics.models.ScreenName
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AccountRepository(
    private val api: BitApi,
    private val prefs: Prefs,
    private val encryptedPrefs: EncryptedPrefs
) {

    suspend fun getCurrentAccount(clearCache: Boolean = false): User {
        if (clearCache) {
            encryptedPrefs.currentAccount = null
        }

        val account = encryptedPrefs.currentAccount
        return if (account == null) {
            val userAccount = api.getAccount()
            encryptedPrefs.currentAccount = userAccount
            userAccount
        } else {
            account
        }
    }


    suspend fun getAvailableAccounts(): List<LocalAccount> {
        val currentAccount = getCurrentAccount()
        val availableAccounts = encryptedPrefs.getUserAccounts().toMutableList()
        if(availableAccounts.isNotEmpty()) {
            availableAccounts.remove(availableAccounts.find { it.uuid == currentAccount.uuid }!!)
        } else {
            addAccount(currentAccount.toLocalAccount(encryptedPrefs.refreshToken ?: ""))
        }
        return availableAccounts
    }

    fun addAccount(localAccount: LocalAccount) {
        encryptedPrefs.addUserAccount(localAccount)
    }

    fun removeAccount(user: User, result: () -> Unit) {
        encryptedPrefs.removeAccount(user) {
            result.invoke()
        }
    }

    fun setNewUser(user: User) {
        encryptedPrefs.currentAccount = user
    }

    suspend fun getAccountByUUIDSuspend(uuid: String) =
        api.getAccountByUUID(uuid)

    suspend fun getUserEmails() {
        if (encryptedPrefs.authenticatedEmails != null) return

        try {
            val response = getUserEmailPages().values.map {
                it.email
            }.toList()
            encryptedPrefs.authenticatedEmails = response.toSet()
            prefs.appVersion = BuildConfig.VERSION_NAME

            sendUserInfoToFirestore()
        } catch (ex: Exception) {
            NonFatalError
                .log(ex, ScreenName.APP)
                .setCustomKey(AnalyticsParameterName.ERROR_NAME, AnalyticsEventNames.EMAIL_COLLECTING_FAIL)
        }
    }

    private fun sendUserInfoToFirestore() {
        with(encryptedPrefs) {
            currentAccount?.let { account ->
                Firebase.firestore.collection(COLLECTION_NAME)
                    .document(account.uuid)
                    .set(UserInfo(account.uuid, prefs.appVersion, authenticatedEmails?.toList()))
            }
        }
    }

    private suspend fun getUserEmailPages(page: String? = null): PagedResponse<Email> {
        var response = api.getUserEmails(page = page)
        var nextPage = response.nextPage

        while (nextPage != null) {
            val tmp = api.getUserEmails(nextPage)
            response = PagedResponse(
                page = tmp.page,
                size = tmp.size,
                pagelen = tmp.pagelen,
                values = response.values.toMutableList().apply {
                    addAll(tmp.values)
                },
                next = tmp.next
            )
            nextPage = tmp.nextPage
        }

        return response
    }

    fun getDonationStatus(): Boolean {
        return prefs.isDonationMade
    }

    fun setDonationStatus(status: Boolean) {
        prefs.isDonationMade = status
    }

    fun setDonationReminderStatus(status: Boolean) {
        prefs.isDonationReminderSet = status
    }

    companion object {
        const val COLLECTION_NAME = "Users"
    }
}
