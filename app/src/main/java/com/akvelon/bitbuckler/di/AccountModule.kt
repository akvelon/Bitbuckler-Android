package com.akvelon.bitbuckler.di

import com.akvelon.bitbuckler.ui.screen.account.switchflow.SwitchAccountPresenter
import com.github.terrakok.cicerone.Router
import org.koin.dsl.module

val accountModule = module {

    factory { (router: Router) ->
        SwitchAccountPresenter(router, get(), get(), get(), get(), get())
    }
}