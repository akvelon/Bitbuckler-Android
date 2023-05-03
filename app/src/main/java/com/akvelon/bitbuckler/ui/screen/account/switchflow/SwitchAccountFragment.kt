package com.akvelon.bitbuckler.ui.screen.account.switchflow

import android.app.Dialog
import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.recyclerview.widget.LinearLayoutManager
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.DialogSwitchAccountBinding
import com.akvelon.bitbuckler.extension.loadCircle
import com.akvelon.bitbuckler.model.entity.LocalAccount
import com.akvelon.bitbuckler.model.entity.User
import com.akvelon.bitbuckler.model.entity.toUser
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.screen.start.StartFragment
import moxy.MvpAppCompatDialogFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

class SwitchAccountFragment
    : MvpAppCompatDialogFragment(), SwitchAccountView {

    private var _binding: DialogSwitchAccountBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var mDialog: Dialog

    val presenter: SwitchAccountPresenter by moxyPresenter {
        get { parametersOf((parentFragment as RouterProvider).router) }
    }

    private lateinit var serviceConnection: CustomTabsServiceConnection
    private var session: CustomTabsSession? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupSession()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.dialog_switch_account, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogSwitchAccountBinding.inflate(LayoutInflater.from(requireContext()))

        mDialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setNegativeButton(R.string.cancel) { _, _ ->
                presenter.logCancelEvent()
            }
            .create()

        mDialog.setOnShowListener {
            presenter.logScreenOpenEvent()

            presenter.loadCurrentUserAccount()
            presenter.loadAvailableAccounts()
        }

        binding.addAnotherAccount.container.setOnClickListener {
            presenter.loadLoginUri()
        }

        return mDialog
    }

    private fun setupSession() {
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
    }

    private fun openLoginPage(loginUri: Uri) {
        dismiss()
        CustomTabsIntent.Builder(session)
            .build()
            .launchUrl(
                requireContext(),
                loginUri
            )
    }

    override fun onCurrentAccountLoaded(currentUser: User) {
        binding.currentAccount.apply {
            userImage.loadCircle(currentUser.links.avatar.href, R.drawable.ic_avatar_placeholder)
            userName.text = currentUser.username
            userNickName.text = currentUser.nickname
        }
    }

    override fun loadExistingAccounts(existingUsers: List<LocalAccount>) {
        val adapter = UserAccountsAdapter(
            existingUsers.map { it.toUser() }, { selectedUser ->
                presenter.safeLogout()
                presenter.clearAllRecent()
                presenter.clearAllTracked()
                presenter.setCurrentUser(existingUsers.find { it.uuid == selectedUser.uuid }!!)

                requireActivity().apply {
                    finish()
                    startActivity(intent)
                }
            }
        ) { userToRemove ->
            presenter.removeUser(userToRemove) {
                presenter.loadAvailableAccounts()
            }
        }
        binding.existingAccountsList.adapter = adapter
        binding.existingAccountsList.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onLoginPageUriLoaded(loginUri: Uri) {
        openLoginPage(loginUri)
    }
}