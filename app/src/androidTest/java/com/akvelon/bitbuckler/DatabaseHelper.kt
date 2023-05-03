/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 30 June 2021
 */

package com.akvelon.bitbuckler

import com.akvelon.bitbuckler.extension.getObjectFromFile
import com.akvelon.bitbuckler.model.datasource.api.bit.response.PagedResponse
import com.akvelon.bitbuckler.model.datasource.database.dao.RecentRepositoryDao
import com.akvelon.bitbuckler.model.entity.repository.Repository
import com.google.gson.Gson
import org.koin.test.KoinTest

class DatabaseHelper(
    private val recentRepositoryDao: RecentRepositoryDao,
    private val gson: Gson
) : KoinTest {

    fun fillRecentRepos(fileName: String = "response-repositories.json") {
        val repositories = gson.getObjectFromFile<PagedResponse<Repository>>(fileName).values

        repositories.forEach { recentRepositoryDao.save(it.toRecent()) }
    }

    fun clearRecentRepos() = recentRepositoryDao.clearAll()
}
