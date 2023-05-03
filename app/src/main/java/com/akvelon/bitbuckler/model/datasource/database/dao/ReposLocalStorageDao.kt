package com.akvelon.bitbuckler.model.datasource.database.dao

import androidx.room.*
import com.akvelon.bitbuckler.model.entity.CachedRepoData
import com.akvelon.bitbuckler.model.entity.RepoLocalData
import java.time.LocalDateTime

@Dao
interface ReposLocalStorageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<CachedRepoData>)

    @Query("DELETE FROM cachedrepodata WHERE workspaceSlug = :workspaceSlug and projectKey = :projectKey")
    suspend fun remove(workspaceSlug: String, projectKey: String)

    @Query("SELECT * FROM cachedrepodata WHERE workspaceSlug = :workspaceSlug and projectKey = :projectKey")
    suspend fun getBy(workspaceSlug: String, projectKey: String): List<CachedRepoData>

    @Query("SELECT * FROM cachedrepodata WHERE isTracked = 1")
    suspend fun getAllTracked(): List<CachedRepoData>

    @Query("SELECT * FROM cachedrepodata WHERE uuid = :uuid")
    suspend fun get(uuid: String): CachedRepoData?

    @Query("UPDATE cachedrepodata SET isTracked = :isTracked WHERE uuid = :uuid")
    suspend fun update(uuid: String, isTracked: Boolean)

    @Query("SELECT COUNT(uuid) FROM cachedrepodata  WHERE isTracked = 1")
    suspend fun getTrackedCounter(): Int

    @Query("SELECT COUNT(uuid) FROM cachedrepodata  WHERE isTracked = 1 AND workspaceSlug = :workspaceSlug")
    suspend fun getTrackedCounterForWorkspace(workspaceSlug: String): Int

    @Query("UPDATE cachedrepodata SET isTracked= :newValue WHERE uuid =:uuid")
    suspend fun updateTrackedFlag(uuid: String, newValue: Boolean)

    @Query("SELECT updatedDate FROM cachedrepodata WHERE workspaceSlug = :workspaceSlug")
    suspend fun lastUpdatedBy(workspaceSlug: String): LocalDateTime

    @Query("DELETE FROM cachedrepodata")
    suspend fun clear()
}