package com.akvelon.bitbuckler.model.repository.pullrequest

import com.akvelon.bitbuckler.model.entity.issueTracker.TrackedIssue
import kotlinx.coroutines.flow.Flow

interface TrackedIssuesRepo {

    fun getAll(): Flow<List<TrackedIssue>>

    suspend fun insert(items: List<TrackedIssue>)

    suspend fun removeBy(uuid: String)

    suspend fun clear()
}