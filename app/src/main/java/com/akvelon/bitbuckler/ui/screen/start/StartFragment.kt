/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 01 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.start

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.FragmentStartBinding
import com.akvelon.bitbuckler.extension.hide
import com.akvelon.bitbuckler.extension.show
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

class StartFragment :
    MvpAppCompatFragment(R.layout.fragment_start),
    StartView {

    private val binding by viewBinding(FragmentStartBinding::bind)

    private lateinit var serviceConnection: CustomTabsServiceConnection
    private var session: CustomTabsSession? = null

    val presenter: StartPresenter by moxyPresenter {
        get { parametersOf((activity as RouterProvider).router) }
    }

    private val appear: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.appear)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        serviceConnection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(
                name: ComponentName,
                client: CustomTabsClient
            ) {
                client.warmup(0L)
                session = client.newSession(null)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
            }
        }

        CustomTabsClient.bindCustomTabsService(
            requireContext(),
            CUSTOM_TABS_PACKAGE,
            serviceConnection
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            appMotto.startAnimation(appear)

            toLogin.setOnClickListener { presenter.onLoginPressed() }
        }
    }

    override fun openLoginPage(loginUri: Uri) {
        try {
            CustomTabsIntent.Builder(session)
                .build()
                .launchUrl(
                    requireContext(),
                    loginUri
                )
        } catch (e: ActivityNotFoundException) {
            presenter.onBrowserNotFound()
            e.printStackTrace()
            AlertDialog.Builder(requireContext())
                .apply {
                    setTitle(R.string.install_browser)
                    setNegativeButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
                }
                .create()
                .show()
        }
    }

    override fun showLoginButton() = with(binding) {
        toLogin.show()
        progressBar.hide()
    }

    fun showProgress() = with(binding) {
        toLogin.hide()
        progressBar.show()
    }

    companion object {
        const val CUSTOM_TABS_PACKAGE = "com.android.chrome"

        fun newInstance() = StartFragment()
    }
}
