/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 27 November 2020
 */

package com.akvelon.bitbuckler.ui.screen.account

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.BuildConfig
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.FragmentAccountBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.User
import com.akvelon.bitbuckler.ui.billing.BillingStatus
import com.akvelon.bitbuckler.ui.billing.services.BillingService
import com.akvelon.bitbuckler.ui.billing.services.SubscriptionsService
import com.akvelon.bitbuckler.ui.billing.utility.SCREEN_TAG_ACCOUNT
import com.akvelon.bitbuckler.ui.view.billing.SubscriptionDialog
import com.akvelon.bitbuckler.ui.helper.CustomLinkMovementMethod
import com.akvelon.bitbuckler.ui.navigation.BackButtonListener
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.night.NightModeType
import com.akvelon.bitbuckler.ui.screen.account.switchflow.SwitchAccountFragment
import com.akvelon.bitbuckler.ui.screen.start.StartFragment
import com.akvelon.bitbuckler.ui.view.billing.DonationDialog
import com.akvelon.bitbuckler.ui.view.MessageDialog
import com.android.billingclient.api.ProductDetails
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

@Suppress("TooManyFunctions")
class AccountFragment :
    MvpAppCompatFragment(R.layout.fragment_account),
    AccountView,
    RouterProvider,
    BackButtonListener {

    private val binding by viewBinding(FragmentAccountBinding::bind)
    private lateinit var serviceConnection: CustomTabsServiceConnection
    private var session: CustomTabsSession? = null

    override val router: Router
        get() = (parentFragment as RouterProvider).router

    val presenter: AccountPresenter by moxyPresenter {
        get { parametersOf(router) }
    }

    private val billingService: BillingService by inject()

    private val confirmLogoutDialog by lazy { createConfirmLogoutDialog() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serviceConnection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(
                name: ComponentName,
                client: CustomTabsClient,
            ) {
                client.warmup(0L)
                session = client.newSession(null)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
            }
        }

        CustomTabsClient.bindCustomTabsService(
            requireContext(),
            StartFragment.CUSTOM_TABS_PACKAGE,
            serviceConnection
        )

        billingService.productDetailsSubject
            .flowWithLifecycle(lifecycle)
            .onEach(this::showProductsDialog)
            .launchIn(lifecycleScope)

        billingService.billingStatusSubject
            .flowWithLifecycle(lifecycle)
            .onEach(this::showBillingStatus)
            .launchIn(lifecycleScope)
    }


    override fun showSubscriptionStatus(isActive: Boolean) {
        with(binding) {
            tvGetPremium.isVisible = !isActive
            tvPremiumLabel.isVisible = isActive
        }
    }

    private fun showBillingStatus(status: BillingStatus) {
        when (status) {
            is BillingStatus.BillingError -> {
                if (status.shouldShowWarningToUser)
                    MessageDialog(requireContext()).showDialog().setValues(status)
            }
            is BillingStatus.Purchase.PurchaseSuccess, is BillingStatus.History.HistorySuccess -> {
                presenter.saveDonation()
                MessageDialog(requireContext()).showDialog().setValues(status)
            }
            else -> MessageDialog(requireContext()).showDialog().setValues(status)
        }
    }

    override fun showDonationStatus() {
        binding.ivDonationCrown.show()
    }

    private fun showProductsDialog(products: List<ProductDetails>) {
        presenter.onDonationDialogOpened()
        DonationDialog(requireContext()).showDialog().bind(products).setType(DonationDialog.PopupType.GIVE_A_GIFT)
            .setActionListener {
                when (it) {
                    DonationDialog.DialogActions.CheckHistory ->
                        billingService.queryAllHistory()
                    is DonationDialog.DialogActions.LaunchBilling ->
                        billingService.launchBilling(requireActivity(), it.selectedProduct)
                    is DonationDialog.DialogActions.ToggleReminder -> Unit
                }
            }.setDismissListener {
                presenter.onDonationDialogClosed()
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            swipeToRefresh.apply {
                setPrimaryColorScheme()
                setOnRefreshListener { presenter.getAccount(clearCache = true) }
            }

            contactUs.setOnClickListener { onContactUsClick() }
            rateApp.setOnClickListener { onRateAppClick() }
            switchAccount.setOnClickListener {
                SwitchAccountFragment().show(childFragmentManager, null)
            }
            privacyPolicy.setOnClickListener {
                presenter.loadPrivacyPolicyContent()
            }
            giveGift.setOnClickListener {
                billingService.startClient(SCREEN_TAG_ACCOUNT)
            }
            tvGetPremium.setOnClickListener {
                presenter.getProductDetails()
            }
            logout.setOnClickListener { presenter.onLogout() }
        }
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    companion object {
        fun newInstance() = AccountFragment()
    }

    override fun showAccount(account: User) =
        with(binding) {
            accountAvatar.loadCircle(account.links.avatar.href, R.drawable.ic_avatar_placeholder)
            displayName.text = account.displayName

            account.location?.let {
                locationIcon.show()
                location.text = account.location
            }

            appVersion.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)

            copyright.movementMethod = CustomLinkMovementMethod(onLinkClickedListener = {
                presenter.onLinkClick()
            })
        }

    override fun showError(error: Throwable) = binding.displayName.snackError(error)

    override fun showConfirm() {
        confirmLogoutDialog.show()
    }

    override fun hideConfirm() {
        confirmLogoutDialog.dismiss()
    }

    override fun hideProgress() {
        binding.swipeToRefresh.isRefreshing = false
    }

    override fun showNightModeDialog(currentMode: Int) {
        val nightModeDialog = AlertDialog.Builder(requireContext())
            .apply {
                setTitle(R.string.night_mode_dialog_title)

                val modes = arrayOf(
                    getString(NightModeType.NIGHT_MODE_NO.title),
                    getString(NightModeType.NIGHT_MODE_YES.title),
                    getString(NightModeType.getDefaultMode().title)
                )

                val currentNightMode = NightModeType.fromValue(currentMode)

                setSingleChoiceItems(modes, currentNightMode.position) { dialog, which ->
                    val nightMode = NightModeType.fromPosition(which)
                    presenter.saveNightMode(nightMode.value)
                    AppCompatDelegate.setDefaultNightMode(nightMode.value)

                    dialog.dismiss()
                }

                setNegativeButton(R.string.night_mode_cancel) { dialog, _ -> dialog.dismiss() }
            }
            .create()

        nightModeDialog.show()
    }

    override fun onDestroy() {
        billingService.endConnection()
        super.onDestroy()
        confirmLogoutDialog.setOnDismissListener(null)
        confirmLogoutDialog.dismiss()
    }

    private fun onContactUsClick() {
        val contactIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(BuildConfig.EMAIL))
        }

        try {
            startActivity(contactIntent)
        } catch (exception: ActivityNotFoundException) {
            presenter.onActivityNotFound(exception)
            binding.root.snackError(exception)
        }
    }

    private fun onRateAppClick() {
        presenter.onRateAppClicked()
        val rateIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(BuildConfig.GOOGLE_PLAY_LINK)
        }

        try {
            startActivity(rateIntent)
        } catch (exception: ActivityNotFoundException) {
            presenter.onActivityNotFound(exception)
            binding.root.snackError(exception)
        }
    }

    private fun createConfirmLogoutDialog() = AlertDialog.Builder(requireContext())
        .setTitle(R.string.confirm_logout_title)
        .setMessage(R.string.confirm_logout_message)
        .setPositiveButton(
            R.string.confirm_logout_yes
        ) { _, _ ->
            presenter.logout()
        }
        .setNegativeButton(
            R.string.confirm_logout_no
        ) { _, _ ->
            presenter.onDismissMessage()
        }
        .setOnDismissListener {
            presenter.onDismissMessage()
        }
        .create()
}
