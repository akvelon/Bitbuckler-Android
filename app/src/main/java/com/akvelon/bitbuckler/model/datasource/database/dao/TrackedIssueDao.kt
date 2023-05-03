package com.akvelon.bitbuckler.model.datasource.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.akvelon.bitbuckler.model.entity.issueTracker.TrackedIssue
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackedIssueDao {

    @Query("SELECT * FROM tracked_issue ORDER BY updatedOn DESC")
    fun getAll(): Flow<List<TrackedIssue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<TrackedIssue>)

    @Query("DELETE FROM tracked_issue WHERE repoUuid = :uuid")
    suspend fun removeBy(uuid: String)

    @Query("DELETE FROM tracked_issue")
    suspend fun clear()
}