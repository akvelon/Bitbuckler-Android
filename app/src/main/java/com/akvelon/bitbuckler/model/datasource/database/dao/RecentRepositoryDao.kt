/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 19 February 2021
 */

package com.akvelon.bitbuckler.model.datasource.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.akvelon.bitbuckler.model.entity.repository.RecentRepository

@Dao
interface RecentRepositoryDao {

    @Query("SELECT * FROM recentRepository ORDER BY lastEntrance DESC")
    suspend fun getAll(): List<RecentRepository>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(recentRepository: RecentRepository)

    @Query(
        """
        DELETE FROM recentRepository 
        WHERE uuid NOT IN 
        (SELECT uuid from recentRepository 
        ORDER BY lastEntrance DESC LIMIT $MAX_LIMIT)
        """
    )
    suspend fun shrinkToLimit()

    @Delete
    suspend fun delete(recentRepository: RecentRepository)

    @Query("DELETE FROM recentRepository")
    suspend fun clearAll()

    companion object {
        const val MAX_LIMIT = 10
    }
}
