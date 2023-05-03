/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Alexander Vinogradov (a.vinogradov@akvelon.com)
 * on 25 February 2022
 */

package com.akvelon.bitbuckler.model.repository

import com.akvelon.bitbuckler.model.datasource.api.bit.BitApi
import com.akvelon.bitbuckler.model.datasource.database.dao.WorkspaceSizeDao
import com.akvelon.bitbuckler.model.datasource.local.EncryptedPrefs
import com.akvelon.bitbuckler.model.datasource.local.Prefs
import com.akvelon.bitbuckler.model.entity.Date
import com.akvelon.bitbuckler.model.entity.NonFatalError
import com.akvelon.bitbuckler.model.entity.WorkspaceSize
import com.akvelon.bitbuckler.model.entity.WorkspaceSizeInfo
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class WorkspaceSizeRepository(
    private val bitApi: BitApi,
    private val workspaceSizeDao: WorkspaceSizeDao,
    private val prefs: Prefs,
    private val encryptedPrefs: EncryptedPrefs
) {
    suspend fun handleWorkspaceSizeEvent(workspaceSlug: String, screenName: String) {
        val workspaceSize: WorkspaceSize? = workspaceSizeDao.get(workspaceSlug)
        if (workspaceSize == null) {
            updateWorkspaceSize(workspaceSlug, screenName)
        } else {
            val now = LocalDateTime.now()
            val days = ChronoUnit.DAYS.between(workspaceSize.lastUpdate, now)
            if (days > UPDATE_PERIOD_IN_DAYS) {
                updateWorkspaceSize(workspaceSlug, screenName)
            }
        }
    }

    private suspend fun updateWorkspaceSize(workspaceSlug: String, screenName: String) {
        try {
            val response = bitApi.getWorkspaceMembers(workspaceSlug)
            val workspaceSize = WorkspaceSize(
                workspaceSlug = workspaceSlug,
                size = response.size,
                lastUpdate = LocalDateTime.now(),
            )
            workspaceSizeDao.save(workspaceSize)

            sendWorkspaceSizeToFirestore(workspaceSlug, response.size)
        } catch (ex: Exception) {
            NonFatalError.log(ex, screenName)
            sendWorkspaceSizeToFirestore(workspaceSlug)
        }
    }

    private fun sendWorkspaceSizeToFirestore(workspaceSlug: String, size: Int? = null) {
        Firebase.firestore
            .collection(COLLECTION_NAME)
            .document(workspaceSlug)
            .get()
            .addOnSuccessListener { document ->
                val account = encryptedPrefs.currentAccount
                val users = mutableListOf<String>().apply {
                    account?.let {
                        add(account.uuid)
                    }
                }
                var totalSize: Int? = size
                if (document.exists()) {
                    val data = document.toObject<WorkspaceSizeInfo>()
                    data?.let {
                        totalSize = size ?: data.size
                        if (data.users.contains(account?.uuid)) {
                            return@addOnSuccessListener
                        } else {
                            users.addAll(data.users)
                        }
                    }
                }
                val localDate = LocalDateTime.now()
                Firebase.firestore.collection(COLLECTION_NAME)
                    .document(workspaceSlug)
                    .set(
                        WorkspaceSizeInfo(
                            workspaceSlug,
                            totalSize,
                            Date(
                                localDate.year,
                                localDate.monthValue,
                                localDate.dayOfMonth,
                                localDate.hour,
                                localDate.minute
                            ),
                            users
                        )
                    )
            }
    }

    companion object {
        const val UPDATE_PERIOD_IN_DAYS = 30
        const val COLLECTION_NAME = "WorkspacesSize"
    }
}
