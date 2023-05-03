/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 27 November 2020
 */

package com.akvelon.bitbuckler.ui.screen.account

import android.os.Bundle
import com.akvelon.bitbuckler.ui.base.BaseTabFragment
import com.akvelon.bitbuckler.ui.navigation.TabTags
import com.akvelon.bitbuckler.ui.screen.Screen
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

class AccountTabFragment : BaseTabFragment() {

    override val tabTag: String = TabTags.ACCOUNT

    override val cicerone: Cicerone<Router> by inject(named(tabTag))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            router.replaceScreen(Screen.account(tabTag))
        }
    }

    override fun backToRoot() {
        router.backTo(Screen.account(tabTag))
    }
}
