/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 07 November 2020
 */

package com.akvelon.bitbuckler.model.datasource.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.akvelon.bitbuckler.model.datasource.database.converter.Converters
import com.akvelon.bitbuckler.model.datasource.database.dao.*
import com.akvelon.bitbuckler.model.entity.*
import com.akvelon.bitbuckler.model.entity.issueTracker.TrackedIssue
import com.akvelon.bitbuckler.model.entity.pullrequest.TrackedPullRequest
import com.akvelon.bitbuckler.model.entity.repository.RecentRepository

@Database(
    entities = [Workspace::class, TrackedPullRequest::class, RecentRepository::class,
        WorkspaceSize::class, RepoLocalData::class, CachedRepoData::class, TrackedIssue::class],
    version = 1,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workspaceDao(): WorkspaceDao

    abstract fun trackedPullRequestDao(): TrackedPullRequestDao

    abstract fun recentRepositoryDao(): RecentRepositoryDao

    abstract fun workspaceSizeDao(): WorkspaceSizeDao

    abstract fun reposLocalStorageDao(): ReposLocalStorageDao

    abstract fun trackedIssueDao(): TrackedIssueDao
}
