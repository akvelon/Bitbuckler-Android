/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 04 June 2021
 */

package com.akvelon.bitbuckler.ui.screen.dialog.branch

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.DialogBranchesBinding
import com.akvelon.bitbuckler.extension.argument

class BranchDialogFragment : DialogFragment(R.layout.dialog_branches) {

    private var _binding: DialogBranchesBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogBranchesBinding.inflate(
            LayoutInflater.from(requireContext())
        )

        with(binding) {
            dialogSourceBranch.text = argument(SOURCE_BRANCH_ARG)
            dialogTargetBranch.text = argument(TARGET_BRANCH_ARG)
            tvCancel.setOnClickListener { dismiss() }
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
        const val SOURCE_BRANCH_ARG = "source_branch_arg"
        const val TARGET_BRANCH_ARG = "target_branch_arg"

        const val TAG = "branch_dialog_tag"

        fun newInstance(sourceBranch: String, targetBranch: String) = BranchDialogFragment().apply {
            arguments = bundleOf(
                SOURCE_BRANCH_ARG to sourceBranch,
                TARGET_BRANCH_ARG to targetBranch
            )
        }
    }
}
