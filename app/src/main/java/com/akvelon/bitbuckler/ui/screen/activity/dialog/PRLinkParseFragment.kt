package com.akvelon.bitbuckler.ui.screen.activity.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.common.EventBus
import com.akvelon.bitbuckler.common.model.Event
import com.akvelon.bitbuckler.common.model.EventNames.PR_DIALOG_CLOSED
import com.akvelon.bitbuckler.databinding.DialogPrLinkBinding
import com.akvelon.bitbuckler.extension.hideKeyboard
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import moxy.MvpAppCompatDialogFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

class PRLinkParseFragment
    : MvpAppCompatDialogFragment(), PrParseView {

    private val binding by viewBinding(DialogPrLinkBinding::bind)

    val presenter: PrLinkPresenter by moxyPresenter {
        get { parametersOf((parentFragment as RouterProvider).router) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_pr_link, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.onPRLinkViewCreated()

        with(binding) {
            tvSubmit.setOnClickListener {
                presenter.logConfirmEvent()
                val url = prLinkText.text.toString()
                presenter.onPullRequestLink(url, isDeepLink = false)
                root.hideKeyboard()
            }

            tvCancel.setOnClickListener {
                presenter.logCancelEvent()
                root.hideKeyboard()
                dialog?.dismiss()
            }
        }
    }

    override fun onDestroy() {
        EventBus.pushEvent(Event(PR_DIALOG_CLOSED))
        super.onDestroy()
    }

    override fun onPrParseError(message: String) {
        if (message.isNotBlank()) {
            binding.errorText.isVisible = true
            binding.errorText.text = message
        }
    }

    override fun onPrParseSuccess() {
        dialog?.dismiss()
    }
}
