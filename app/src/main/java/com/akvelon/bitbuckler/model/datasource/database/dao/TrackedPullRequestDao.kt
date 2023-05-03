/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 28 January 2021
 */

package com.akvelon.bitbuckler.model.datasource.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequestState
import com.akvelon.bitbuckler.model.entity.pullrequest.TrackedPullRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackedPullRequestDao {

    @Query("SELECT * FROM trackedpullrequest ORDER BY updatedOn DESC")
    fun getAll(): Flow<List<TrackedPullRequest>>

    @Query("SELECT * FROM trackedpullrequest ORDER BY updatedOn DESC")
    suspend fun getAllAsync(): List<TrackedPullRequest>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(trackedPullRequests: List<TrackedPullRequest>)

    @Query("DELETE FROM trackedpullrequest WHERE repoUuid = :uuid")
    fun removeBy(uuid: String)

    @Query("""UPDATE trackedpullrequest SET state = :state 
        WHERE idInRepository = :idInRepository 
        AND workspaceSlug = :workspaceSlug
        AND repositorySlug = :repositorySlug""")
    fun updateState(workspaceSlug: String,
                    repositorySlug: String,
                    idInRepository: Int,
                    state: PullRequestState)

    @Query("DELETE FROM trackedpullrequest")
    fun clear()

    @Query(
        """
            DELETE FROM trackedPullRequest
            WHERE workspaceSlug == :workspaceSlug 
            AND repositorySlug == :repositorySlug 
            AND idInRepository == :idInRepository 
            """
    )
    fun delete(
        workspaceSlug: String,
        repositorySlug: String,
        idInRepository: Int
    )
}
