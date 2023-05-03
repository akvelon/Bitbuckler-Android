/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 07 December 2020
 */

package com.akvelon.bitbuckler.di

import com.akvelon.bitbuckler.ui.navigation.TabTags
import com.github.terrakok.cicerone.Cicerone
import org.koin.core.qualifier.named
import org.koin.dsl.module

val navigationModule = module {

    single {
        cicerone()
    }

    single(named(TabTags.ACTIVITY)) {
        cicerone()
    }

    single(named(TabTags.WORKSPACES)) {
        cicerone()
    }

    single(named(TabTags.ACCOUNT)) {
        cicerone()
    }
}

fun cicerone() = Cicerone.create()
