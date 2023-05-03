/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 27 November 2020
 */

package com.akvelon.bitbuckler.ui.screen.activity

import android.os.Bundle
import com.akvelon.bitbuckler.ui.base.BaseTabFragment
import com.akvelon.bitbuckler.ui.navigation.BottomNavListener
import com.akvelon.bitbuckler.ui.navigation.TabTags
import com.akvelon.bitbuckler.ui.screen.Screen
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

class ActivityTabFragment :
    BaseTabFragment(),
    BottomNavListener {

    override val tabTag: String = TabTags.ACTIVITY

    override val cicerone: Cicerone<Router> by inject(named(tabTag))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            router.replaceScreen(Screen.activityList(tabTag))
        }
    }

    override fun backToRoot() {
        router.backTo(Screen.activityList(tabTag))
    }

    override fun setBottomNavVisibility(show: Boolean) {
        (parentFragment as BottomNavListener).setBottomNavVisibility(show)
    }
}
