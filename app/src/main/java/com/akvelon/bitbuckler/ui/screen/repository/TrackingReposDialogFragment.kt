package com.akvelon.bitbuckler.ui.screen.repository

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.FragmentTrackRepoBinding
import com.akvelon.bitbuckler.databinding.FragmentTrackingReposBinding
import com.akvelon.bitbuckler.ui.navigation.RouterProvider
import com.akvelon.bitbuckler.ui.screen.Screen
import moxy.MvpAppCompatDialogFragment
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

class TrackingReposDialogFragment
    : MvpAppCompatDialogFragment(), TrackingInfoView {

    private var _binding: FragmentTrackingReposBinding? = null
    private val binding
        get() = _binding!!

    private val presenter: TrackingInfoPresenter by moxyPresenter {
        get { parametersOf((parentFragment as RouterProvider).router) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tracking_repos, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        presenter.trackReposPopupOpened()
        _binding = FragmentTrackingReposBinding.inflate(
            LayoutInflater.from(requireContext())
        )

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setNegativeButton(R.string.cancel) { _, _ ->
                presenter.trackReposCancelAction()
                dismiss()
            }
            .setPositiveButton(R.string.track_choose) { _, _ ->
                presenter.trackReposConfirmClicked()
                presenter.navigateToTrackRepoScreen()
                dismiss()
            }
            .create()

        binding.check.setOnCheckedChangeListener { _, isChecked ->
            presenter.setTrackReposFlag(isChecked)
            presenter.trackReposDoNotShowAgainAction(isChecked)
        }

        return dialog
    }
}