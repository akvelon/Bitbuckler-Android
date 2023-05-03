/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 03 June 2021
 */

package com.akvelon.bitbuckler.screen

import androidx.test.espresso.action.GeneralLocation
import com.agoda.kakao.navigation.KNavigationView
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.ui.screen.main.MainFragment
import com.kaspersky.kaspresso.screens.KScreen

object MainScreen : KScreen<MainScreen>() {
    override val layoutId = R.layout.fragment_main
    override val viewClass = MainFragment::class.java

    private val bottomNavigation = KNavigationView { withId(R.id.bottomNavigationView) }

    fun goToAccount() = bottomNavigation.click(GeneralLocation.TOP_RIGHT)

    fun goToWorkspaces() = bottomNavigation.click(GeneralLocation.CENTER)

    fun goToActivity() = bottomNavigation.click(GeneralLocation.TOP_LEFT)
}
