/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 04 May 2021
 */

package com.akvelon.bitbuckler.test

import androidx.test.core.app.launchActivity
import com.akvelon.bitbuckler.base.BaseTest
import com.akvelon.bitbuckler.screen.ActivityListScreen
import com.akvelon.bitbuckler.ui.screen.AppActivity
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActivityListFragmentTest : BaseTest() {

    @Before
    fun launch() {
        prefsHelper.preparePrefsForTest()

        launchActivity<AppActivity>()
    }

    @After
    fun clearPrefs() = prefsHelper.clearPrefs()

    @Test
    fun activityListShouldHaveSizeTwo() {
        run {
            ActivityListScreen {
                recyclerViewActivity.apply {
                    isDisplayed()
                    hasSize(2)

                    firstChild<ActivityListScreen.ActivityItem> {
                        title.hasText("B-1")
                    }
                    childAt<ActivityListScreen.ActivityItem>(1) {
                        author.hasText("John Doe")
                    }
                }
            }
        }
    }
}
