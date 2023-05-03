/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 04 June 2021
 */

package com.akvelon.bitbuckler.ui.screen.dialog.decline

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.akvelon.bitbuckler.R

class DeclineActionDialogFragment : DialogFragment() {

    private lateinit var listener: DeclineActionDialogListener

    interface DeclineActionDialogListener {

        fun declineAction()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = parentFragment as DeclineActionDialogListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) = AlertDialog.Builder(requireContext())
        .setTitle(R.string.confirm_decline_title)
        .setMessage(R.string.confirm_decline_message)
        .setPositiveButton(
            R.string.confirm_decline_yes
        ) { _, _ ->
            listener.declineAction()
        }
        .setNegativeButton(
            R.string.confirm_decline_no,
            null
        )
        .create()

    companion object {
        const val TAG = "decline_action_tag"
    }
}
