/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 08 June 2021
 */

package com.akvelon.bitbuckler.base

import com.akvelon.bitbuckler.PrefsHelper
import com.akvelon.bitbuckler.mock.MockServer
import com.akvelon.bitbuckler.mock.mockDatabaseModule
import com.akvelon.bitbuckler.mock.mockNetworkModule
import com.akvelon.bitbuckler.mock.mockPrefsModule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.AfterClass
import org.junit.BeforeClass
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.test.KoinTest
import org.koin.test.inject

abstract class BaseTest :
    TestCase(),
    KoinTest {

    companion object {
        private val modules = listOf(mockNetworkModule, mockPrefsModule, mockDatabaseModule)

        @BeforeClass
        @JvmStatic
        fun setUp() {
            loadKoinModules(modules)

            MockServer.setUp()
        }

        @AfterClass
        @JvmStatic
        fun cleanUp() {
            MockServer.shutDown()

            unloadKoinModules(modules)
        }
    }

    val prefsHelper: PrefsHelper by inject()
}
