/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 09 June 2021
 */

package com.akvelon.bitbuckler.test

import androidx.test.core.app.launchActivity
import com.akvelon.bitbuckler.DatabaseHelper
import com.akvelon.bitbuckler.base.BaseTest
import com.akvelon.bitbuckler.screen.MainScreen
import com.akvelon.bitbuckler.screen.RepositoryDetailsScreen
import com.akvelon.bitbuckler.screen.WorkspaceListScreen
import com.akvelon.bitbuckler.ui.screen.AppActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.test.inject

class WorkspaceListFragmentTest : BaseTest() {

    private val databaseHelper: DatabaseHelper by inject()

    @Before
    fun launch() = prefsHelper.preparePrefsForTest()

    @After
    fun clearPrefs() = prefsHelper.clearPrefs()

    @Test
    fun recentReposShouldBeHidden() {
        init {
            launchWorkspaces()
        }.run {
            WorkspaceListScreen {
                recyclerViewRepositoryCards {
                    isNotDisplayed()
                }
            }
        }
    }

    @After
    fun clearRecentRepos() = databaseHelper.clearRecentRepos()

    @Test
    fun recentReposShouldBeDisplayed() {
        init {
            databaseHelper.fillRecentRepos()

            launchWorkspaces()
        }.run {
            step("Clicks on the last Recent Repository") {
                WorkspaceListScreen {
                    recyclerViewRepositoryCards {
                        isDisplayed()

                        scrollToEnd()
                        lastChild<WorkspaceListScreen.RecentRepoItem> {
                            click()
                        }
                    }
                }
            }
            step("Opens RepositoryDetailsScreen and clicking back button") {
                RepositoryDetailsScreen {
                    backButton.click()
                }
            }
            step(
                "Reopens WorkspaceListScreen and checking last opened Repository " +
                    "was added to Recent Repositories"
            ) {
                WorkspaceListScreen {
                    recyclerViewRepositoryCards.firstChild<WorkspaceListScreen.RecentRepoItem> {
                        this.name.hasText("First")
                    }
                }
            }
        }
    }

    private fun launchWorkspaces() {
        launchActivity<AppActivity>()

        MainScreen {
            goToWorkspaces()
        }
    }
}
