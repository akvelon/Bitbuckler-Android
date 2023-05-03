/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 04 June 2021
 */

package com.akvelon.bitbuckler.ui.screen.dialog.merge

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.DialogMergeActionBinding
import com.akvelon.bitbuckler.extension.argument
import com.akvelon.bitbuckler.extension.hideKeyboard
import com.akvelon.bitbuckler.extension.hideSpinnerDropDown
import com.akvelon.bitbuckler.model.entity.args.MergeActionArgs
import com.akvelon.bitbuckler.model.entity.pullrequest.action.MergeStrategy
import com.akvelon.bitbuckler.model.entity.pullrequest.action.MergeStrategyCollection
import com.akvelon.bitbuckler.model.entity.pullrequest.action.MergeStrategyItem

class MergeActionDialogFragment : DialogFragment(R.layout.dialog_merge_action), SpinnerItemClickListener {

    private var _binding: DialogMergeActionBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var listener: MergeActionDialogListener

    interface MergeActionDialogListener {
        fun mergeAction(message: String, closedSourceBranch: Boolean, mergeStrategy: MergeStrategy)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = parentFragment as MergeActionDialogListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogMergeActionBinding.inflate(
            LayoutInflater.from(requireContext())
        )
        val args = argument<MergeActionArgs>(MERGE_ACTION_ARGS)

        createDialogView(args)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setPositiveButton(
                R.string.confirm_merge_yes
            ) { _, _ ->
                with(binding) {
                    listener.mergeAction(
                        editTextCommit.text.toString(),
                        closeSourceBranch.isChecked,
                        (mergeStrategySpinner.selectedItem as MergeStrategyItem).strategy
                    )
                }
            }
            .setNegativeButton(
                R.string.confirm_merge_no,
                null
            )
            .create()

        binding.run {
            container.setOnClickListener {
                container.hideKeyboard()
                editTextCommit.clearFocus()
            }
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun createDialogView(args: MergeActionArgs) =
        with(binding) {
            mergeSourceBranch.text = args.sourceBranch
            mergeTargetBranch.text = args.targetBranch

            editTextCommit.append(
                getString(
                    R.string.confirm_merge_commit_template,
                    args.sourceBranch,
                    args.pullRequestId,
                    args.pullRequestTitle
                )
            )

            mergeStrategySpinner.adapter = MergeStrategyAdapter(MergeStrategyCollection.items, this@MergeActionDialogFragment)

            with(mergeStrategyFake) {
                setOnClickListener { mergeStrategySpinner.performClick() }
                text = (mergeStrategySpinner.selectedItem as MergeStrategyItem).strategy.title
            }

            closeSourceBranch.isChecked = args.closeSourceBranch
        }

    companion object {
        const val MERGE_ACTION_ARGS = "merge_args"

        const val TAG = "merge_action_dialog_tag"

        fun newInstance(mergeActionArgs: MergeActionArgs) = MergeActionDialogFragment().apply {
            arguments = bundleOf(
                MERGE_ACTION_ARGS to mergeActionArgs
            )
        }
    }

    override fun onClick(title: String) = with(binding) {
        mergeStrategyFake.text = title
        mergeStrategySpinner.hideSpinnerDropDown()
    }
}
