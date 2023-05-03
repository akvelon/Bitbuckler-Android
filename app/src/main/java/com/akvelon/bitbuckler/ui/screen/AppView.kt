/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 02 December 2020
 */
package com.akvelon.bitbuckler.ui.screen

import com.akvelon.bitbuckler.ui.billing.BillingStatus
import com.android.billingclient.api.ProductDetails
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface AppView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showLoginProgress()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showLoginButton()

    @StateStrategyType(SkipStrategy::class)
    fun showErrorMessage(error: Throwable)

    @StateStrategyType(SkipStrategy::class)
    fun showSubscriptionsDialog(product: ProductDetails)

    @StateStrategyType(SkipStrategy::class)
    fun onSubscriptionStatus(status: BillingStatus)

}
