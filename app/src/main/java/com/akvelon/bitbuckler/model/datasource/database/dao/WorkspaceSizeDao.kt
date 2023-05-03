/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Alexander Vinogradov (a.vinogradov@akvelon.com)
 * on 25 February 2022
 */

package com.akvelon.bitbuckler.model.datasource.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.akvelon.bitbuckler.model.entity.WorkspaceSize

@Dao
interface WorkspaceSizeDao {

    @Query("SELECT * FROM workspaceSize WHERE workspaceSlug = :workspaceSlug")
    fun get(workspaceSlug: String): WorkspaceSize?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(workspaceSize: WorkspaceSize)
}
