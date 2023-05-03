/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 04 June 2021
 */

package com.akvelon.bitbuckler.ui.screen.dialog.builds

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.DialogBuildsBinding
import com.akvelon.bitbuckler.extension.argument
import com.akvelon.bitbuckler.model.entity.statuses.Status

class BuildsDialogFragment : DialogFragment(R.layout.dialog_builds) {

    private var _binding: DialogBuildsBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogBuildsBinding.inflate(
            LayoutInflater.from(requireContext())
        )

        binding.recyclerViewBuilds.apply {
            setHasFixedSize(true)
            adapter = BuildsAdapter(argument(BUILDS_ARGS))
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    companion object {
        const val BUILDS_ARGS = "builds_args"

        const val TAG = "builds_dialog_tag"

        fun newInstance(builds: List<Status>) = BuildsDialogFragment().apply {
            arguments = bundleOf(
                BUILDS_ARGS to builds
            )
        }
    }
}
