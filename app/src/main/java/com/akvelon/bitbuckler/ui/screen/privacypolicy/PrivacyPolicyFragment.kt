package com.akvelon.bitbuckler.ui.screen.privacypolicy

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.FragmentPrivacyPolicyBinding
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

const val LOAD_TYPE_DEFAULT = "default"
const val LOAD_TYPE_ACCOUNT = "account"
const val TYPE = "type"

class PrivacyPolicyFragment :
    MvpAppCompatFragment(R.layout.fragment_privacy_policy),
    PrivacyPolicyView {

    private val cicerone: Cicerone<Router> by inject()

    private val presenter: PrivacyPolicyPresenter by moxyPresenter {
        get { parametersOf(if(arguments?.getString(TYPE) == LOAD_TYPE_DEFAULT) cicerone.router else (parentFragment as RouterProvider).router) }
    }

    private val binding by viewBinding(FragmentPrivacyPolicyBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.screenOpenedEvent()
        binding.progressBar.isVisible = true
        presenter.loadWebContent()
        setup()
    }

    private fun setup() {
        when(arguments?.getString(TYPE)) {
            LOAD_TYPE_DEFAULT -> {
                binding.close.isVisible = false
                binding.accept.isVisible = true
                binding.close.setOnClickListener(null)
            }
            LOAD_TYPE_ACCOUNT -> {
                binding.close.isVisible = true
                binding.accept.isVisible = false
                binding.close.setOnClickListener {
                    presenter.onPageClose()
                }
            }
        }
    }

    override fun loadWebContent(data: String) {
        binding.webView.loadDataWithBaseURL(null, data, "text/html", "UTF-8", null)
        binding.webView.webViewClient = PrivacyPolicyWebClient {
            binding.progressBar.isVisible = false
            enableAcceptButton()
        }
    }

    override fun enableAcceptButton() {
        binding.accept.setOnClickListener {
            presenter.setUserAccepted()
        }
        binding.accept.setTextColor(ContextCompat.getColor(requireContext(), R.color.azure))
    }

    companion object {
        fun newInstance(type: String) = PrivacyPolicyFragment().apply {
            arguments = bundleOf(
                TYPE to type
            )
        }
    }
}