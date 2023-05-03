/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 26 May 2021
 */

package com.akvelon.bitbuckler

import com.akvelon.bitbuckler.extension.getObjectFromFile
import com.akvelon.bitbuckler.model.datasource.local.EncryptedPrefs
import com.akvelon.bitbuckler.model.entity.User
import com.google.gson.Gson

class PrefsHelper(
    private val encryptedPrefs: EncryptedPrefs,
    private val gson: Gson
) {

    fun fillToken(token: String = "fakeAccessToken") {
        encryptedPrefs.accessToken = token
    }

    fun fillUserFromFile(fileName: String = "response-user.json") {
        encryptedPrefs.currentAccount = gson.getObjectFromFile<User>(fileName)
    }

    fun preparePrefsForTest() {
        fillToken()
        fillUserFromFile()
    }

    fun clearPrefs() = encryptedPrefs.clear()

    fun getAccessToken() = encryptedPrefs.accessToken

    fun getAccount() = encryptedPrefs.currentAccount
}
