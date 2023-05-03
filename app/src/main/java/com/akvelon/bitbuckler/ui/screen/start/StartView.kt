/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 01 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.start

import android.net.Uri
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface StartView : MvpView {

    @StateStrategyType(SkipStrategy::class)
    fun openLoginPage(loginUri: Uri)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showLoginButton()
}
