package com.akvelon.bitbuckler.ui.screen.account.switchflow

import android.net.Uri
import com.akvelon.bitbuckler.model.entity.LocalAccount
import com.akvelon.bitbuckler.model.entity.User
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

interface SwitchAccountView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun onCurrentAccountLoaded(currentUser: User)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun loadExistingAccounts(existingUsers: List<LocalAccount>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun onLoginPageUriLoaded(loginUri: Uri)
}