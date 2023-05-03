/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 03 June 2021
 */

package com.akvelon.bitbuckler.test

import androidx.test.core.app.launchActivity
import com.akvelon.bitbuckler.base.BaseTest
import com.akvelon.bitbuckler.screen.AccountScreen
import com.akvelon.bitbuckler.screen.MainScreen
import com.akvelon.bitbuckler.ui.screen.AppActivity
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class AccountFragmentTest : BaseTest() {

    @Before
    fun goToAccount() {
        prefsHelper.preparePrefsForTest()

        launchActivity<AppActivity>()

        MainScreen {
            goToAccount()
        }
    }

    @After
    fun clearPrefs() = prefsHelper.clearPrefs()

    @Test
    fun accountPropertiesAreDisplayed() {
        run {
            AccountScreen {
                displayName {
                    isDisplayed()
                    hasText("Jane Lane")
                }
                avatar.isDisplayed()
                location {
                    isDisplayed()
                    hasText("Yaroslavl, Russia")
                }
            }
        }
    }

    @Test
    fun prefsShouldBeEmptyAfterLogout() {
        run {
            step("Logout") {
                AccountScreen {
                    logout {
                        isDisplayed()
                        click()
                    }
                    confirmDialog.positiveButton.click()
                }
            }
            step("Check that prefs are clear") {
                assertThat(prefsHelper.getAccessToken()).isNull()
                assertThat(prefsHelper.getAccount()).isNull()
            }
        }
    }
}
