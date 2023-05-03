/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 01 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commitNow
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.common.EventBus
import com.akvelon.bitbuckler.common.model.Event
import com.akvelon.bitbuckler.common.model.EventNames
import com.akvelon.bitbuckler.common.model.EventNames.TAB_ACTIVITY_TAPPED
import com.akvelon.bitbuckler.common.model.EventNames.TAB_PROFILE_TAPPED
import com.akvelon.bitbuckler.common.model.EventNames.TAB_WORKSPACES_TAPPED
import com.akvelon.bitbuckler.databinding.FragmentMainBinding
import com.akvelon.bitbuckler.extension.hide
import com.akvelon.bitbuckler.extension.show
import com.akvelon.bitbuckler.ui.base.BaseTabFragment
import com.akvelon.bitbuckler.ui.billing.services.BillingService
import com.akvelon.bitbuckler.ui.billing.BillingStatus
import com.akvelon.bitbuckler.ui.billing.utility.SCREEN_TAG_MAIN
import com.akvelon.bitbuckler.ui.navigation.BackButtonListener
import com.akvelon.bitbuckler.ui.navigation.BottomNavListener
import com.akvelon.bitbuckler.ui.navigation.TabTags
import com.akvelon.bitbuckler.ui.screen.Screen
import com.akvelon.bitbuckler.ui.view.billing.DonationDialog
import com.akvelon.bitbuckler.ui.view.MessageDialog
import com.android.billingclient.api.ProductDetails
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class MainFragment :
    MvpAppCompatFragment(R.layout.fragment_main),
    MainView,
    BackButtonListener,
    BottomNavListener {

    private val binding by viewBinding(FragmentMainBinding::bind)

    val presenter: MainPresenter by moxyPresenter { get() }

    private val billingService: BillingService by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            bottomNavigationView.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.workspaces -> {
                        changeTab(TabTags.WORKSPACES)
                        lifecycleScope.launch { EventBus.pushEvent(Event(TAB_WORKSPACES_TAPPED)) }
                        true
                    }
                    R.id.activity -> {
                        changeTab(TabTags.ACTIVITY)
                        lifecycleScope.launch { EventBus.pushEvent(Event(TAB_ACTIVITY_TAPPED)) }
                        true
                    }
                    R.id.account -> {
                        changeTab(TabTags.ACCOUNT)
                        lifecycleScope.launch { EventBus.pushEvent(Event(TAB_PROFILE_TAPPED)) }
                        true
                    }
                    else -> false
                }
            }

            if (savedInstanceState == null) {
                bottomNavigationView.selectedItemId = R.id.activity
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        billingService.productDetailsSubject
            .flowWithLifecycle(lifecycle)
            .onEach(this::showProductsDialog)
            .launchIn(lifecycleScope)

        billingService.billingStatusSubject
            .flowWithLifecycle(lifecycle)
            .onEach(this::showBillingStatus)
            .launchIn(lifecycleScope)
    }

    private fun showBillingStatus(status: BillingStatus) {
        when (status) {
            is BillingStatus.BillingError -> {
                if (status.shouldShowWarningToUser)
                    MessageDialog(requireContext()).showDialog().setValues(status)
            }
            is BillingStatus.Purchase.PurchaseSuccess -> {
                presenter.saveDonation()
                MessageDialog(requireContext()).showDialog().setValues(status)
            }
            else -> MessageDialog(requireContext()).showDialog().setValues(status)
        }
        billingService.endConnection()
    }

    private fun showProductsDialog(products: List<ProductDetails>) {
        presenter.onDonationDialogOpened()
        DonationDialog(requireContext()).showDialog().bind(products).setType(DonationDialog.PopupType.SELF_POPUP)
            .setActionListener {
                when (it) {
                    is DonationDialog.DialogActions.LaunchBilling ->
                        billingService.launchBilling(requireActivity(), it.selectedProduct)
                    is DonationDialog.DialogActions.ToggleReminder ->
                        presenter.setDonationReminderStatus(it.enabled)
                    else -> Unit
                }
            }.setDismissListener {
                billingService.endConnection()
                presenter.onDonationDialogClosed()
            }
    }

    override fun showDonationDialog() {
        billingService.startClient(SCREEN_TAG_MAIN)
    }

    fun navigateToActivityListScreen() {
        binding.bottomNavigationView.selectedItemId = R.id.activity
    }

    private fun changeTab(tabTag: String) {
        val currentFragment = getCurrentFragment()

        val targetFragment = childFragmentManager.findFragmentByTag(tabTag)

        if (currentFragment != null && targetFragment != null && currentFragment === targetFragment) {
            (targetFragment as BaseTabFragment).backToRoot()
        }

        lifecycleScope.launch { EventBus.pushEvent(Event(EventNames.WORKSPACE_REOPENED)) }

        childFragmentManager.commitNow {
            setReorderingAllowed(true)

            if (targetFragment == null) {
                add(
                    R.id.mainContainer,
                    Screen.tab(tabTag).createFragment(childFragmentManager.fragmentFactory),
                    tabTag
                )
            }

            if (currentFragment != null) hide(currentFragment)

            if (targetFragment != null) show(targetFragment)
        }
    }

    override fun onBackPressed() {
        val currentFragment = getCurrentFragment()

        if (currentFragment != null && currentFragment is BackButtonListener) {
            currentFragment.onBackPressed()
        } else {
            activity?.finish()
        }
    }

    override fun showBottomNav() {
        binding.bottomNavigationView.show()
    }

    override fun hideBottomNav() {
        binding.bottomNavigationView.hide()
    }

    override fun setBottomNavVisibility(show: Boolean) {
        if (show) presenter.showBottomNav() else presenter.hideBottomNav()
    }

    override fun showRateDialog() {
        val reviewManager = ReviewManagerFactory.create(requireContext())

        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful && isAdded) {
                reviewManager.launchReviewFlow(requireActivity(), task.result)
            }
        }
    }

    private fun getCurrentFragment() =
        childFragmentManager.fragments.find {
            it.isVisible
        }

    companion object {
        fun newInstance() = MainFragment()
    }
}
