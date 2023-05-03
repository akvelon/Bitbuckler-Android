/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 23 November 2020
 */

package com.akvelon.bitbuckler.model.datasource.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.akvelon.bitbuckler.model.entity.Workspace

@Dao
interface WorkspaceDao {

    @Query("SELECT * FROM workspace")
    fun getAll(): List<Workspace>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(visits: List<Workspace>)

    @Delete
    fun clear(visits: List<Workspace>)
}
