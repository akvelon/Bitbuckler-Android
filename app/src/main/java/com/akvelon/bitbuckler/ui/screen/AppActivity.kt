/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 07 November 2020
 */

package com.akvelon.bitbuckler.ui.screen

import android.content.Intent
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ActivityAppBinding
import com.akvelon.bitbuckler.extension.snackError
import com.akvelon.bitbuckler.ui.ads.AdsPresenter
import com.akvelon.bitbuckler.ui.ads.AdsView
import com.akvelon.bitbuckler.ui.billing.BillingStatus
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.screen.main.MainFragment
import com.akvelon.bitbuckler.ui.screen.start.StartFragment
import com.akvelon.bitbuckler.ui.view.MessageDialog
import com.akvelon.bitbuckler.ui.view.billing.SubscriptionDialog
import com.android.billingclient.api.ProductDetails
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class AppActivity :
    MvpAppCompatActivity(),
    AppView,
    AdsView,
    RouterProvider {

    private val binding by viewBinding(ActivityAppBinding::bind)

    private val adsPresenter: AdsPresenter by moxyPresenter {
        get { parametersOf(router) }
    }

    val presenter: AppPresenter by moxyPresenter {
        get { parametersOf(router) }
    }

    private val cicerone: Cicerone<Router> by inject()

    override val router
        get() = cicerone.router

    private val navigator = AppNavigator(this, R.id.appContainer)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adsPresenter.checkIfAdsAreEnabled()

        installSplashScreen()
        setContentView(R.layout.activity_app)
        /*
        We set a new root screen if it is the first run of activity.
        Otherwise, fragment manager can restore states of the screens
         */
        if (savedInstanceState == null) {
            presenter.setNewRootScreen()
        }
    }

    override fun onSubscriptionStatus(status: BillingStatus) {
        when (status) {
            BillingStatus.History.HistorySuccess -> {
                presenter.saveSubscription()
            }
            BillingStatus.Purchase.PurchaseSuccess -> {
                presenter.saveSubscription()
                MessageDialog(this).showDialog()
                    .setValues(
                        R.string.subscription_success_title,
                        R.string.subscription_success_subtitle,
                        R.drawable.ic_premium
                    )
            }
            BillingStatus.PreviousPurchasePending -> {
                MessageDialog(this).showDialog().setValues(
                    R.string.subscription_failure,
                    R.string.subscription_pending,
                    R.drawable.ic_failure
                )
            }
            is BillingStatus.BillingError -> {
                if (status.shouldShowWarningToUser)
                    MessageDialog(this).showDialog().setValues(
                        R.string.subscription_failure,
                        R.string.donation_failed_message,
                        R.drawable.ic_failure
                    )
            }
            else -> Unit
        }
    }

    override fun showSubscriptionsDialog(product: ProductDetails) {
        SubscriptionDialog(this).showDialog().setData(product) { chosenPlan ->
            presenter.launchBilling(this, product, chosenPlan)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        /*
        When authorization in CustomTabs or in Browser will be passed, the tab will create
        new intent with code from Bitbucket. Our activity will catch this intent and extract the code.
         */
        this.intent = intent
        login(intent)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        cicerone.getNavigatorHolder().setNavigator(navigator)
    }

    override fun onPause() {
        cicerone.getNavigatorHolder().removeNavigator()
        super.onPause()
    }

    override fun showLoginProgress() {
        val currentFragment = getCurrentFragment()

        if (currentFragment is StartFragment) {
            currentFragment.showProgress()
        }
    }

    override fun showLoginButton() {
        val currentFragment = getCurrentFragment()

        if (currentFragment is StartFragment) {
            currentFragment.showLoginButton()
        }
    }

    override fun showErrorMessage(error: Throwable) {
        binding.root.snackError(error)
    }

    override fun onBackPressed() {
        val currentFragment = getCurrentFragment()

        if (currentFragment is MainFragment) {
            currentFragment.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }

    private fun getCurrentFragment() = supportFragmentManager.findFragmentById(R.id.appContainer)

    private fun login(intent: Intent?) {
        try {
            intent?.data?.getQueryParameter(AUTH_CODE_KEY)?.let {
                presenter.safeLogout()
                val addedAccount = presenter.checkIfUserAddedNewAccount()
                presenter.login(it, addedAccount)
            }
        } catch (_: Exception) {

        }
    }

    companion object {
        const val AUTH_CODE_KEY = "code"
    }
}
