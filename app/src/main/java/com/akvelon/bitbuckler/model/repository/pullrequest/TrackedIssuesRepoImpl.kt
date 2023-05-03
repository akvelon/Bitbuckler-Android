package com.akvelon.bitbuckler.model.repository.pullrequest

import com.akvelon.bitbuckler.model.datasource.database.dao.TrackedIssueDao
import com.akvelon.bitbuckler.model.entity.issueTracker.TrackedIssue
import kotlinx.coroutines.flow.Flow

class TrackedIssuesRepoImpl(
    private val trackedIssuesDao: TrackedIssueDao,
) : TrackedIssuesRepo {
    override fun getAll(): Flow<List<TrackedIssue>> {
        return trackedIssuesDao.getAll()
    }

    override suspend fun insert(items: List<TrackedIssue>) {
        trackedIssuesDao.insert(items)
    }

    override suspend fun removeBy(uuid: String) {
        trackedIssuesDao.removeBy(uuid)
    }

    override suspend fun clear() {
        trackedIssuesDao.clear()
    }
}