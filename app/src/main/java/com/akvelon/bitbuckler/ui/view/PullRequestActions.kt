/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 16 February 2021
 */

package com.akvelon.bitbuckler.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.LayoutPullRequestActionsBinding
import com.akvelon.bitbuckler.extension.setBackgroundTint
import com.akvelon.bitbuckler.extension.setDrawableTint
import com.akvelon.bitbuckler.extension.show
import com.akvelon.bitbuckler.model.entity.User
import com.akvelon.bitbuckler.model.entity.participant.ParticipantState
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequest

class PullRequestActions(
    context: Context,
    attrs: AttributeSet?,
) : ConstraintLayout(context, attrs) {

    var onApproveClickListener: (() -> Unit)? = null
    var onRevokeApproveClickListener: (() -> Unit)? = null

    var onRequestChangesClickListener: (() -> Unit)? = null
    var onRevokeRequestChangesClickListener: (() -> Unit)? = null

    var isEnabledActions
        get() = with(binding) {
            merge.isEnabled && approve.isEnabled && decline.isEnabled
        }
        set(value) {
            with(binding) {
                merge.isEnabled = value
                approve.isEnabled = value
                decline.isEnabled = value
            }
        }

    @ColorInt
    private val approvedBackground: Int

    @ColorInt
    private val requestChangesBackground: Int

    @ColorInt
    private val defaultBackground: Int

    @ColorInt
    private val defaultTextColor: Int

    @ColorInt
    private val pressedTextColor: Int

    private var binding = LayoutPullRequestActionsBinding.bind(
        inflate(
            context,
            R.layout.layout_pull_request_actions,
            this
        )
    )

    init {
        context.obtainStyledAttributes(attrs, R.styleable.PullRequestActions).run {
            approvedBackground = getColor(R.styleable.PullRequestActions_approvedBackground, 0)
            requestChangesBackground = getColor(R.styleable.PullRequestActions_requestChangesBackground, 0)
            defaultBackground = getColor(R.styleable.PullRequestActions_prActionsDefaultBackground, 0)
            defaultTextColor = getColor(R.styleable.PullRequestActions_prActionsDefaultTextColor, 0)
            pressedTextColor = getColor(R.styleable.PullRequestActions_prActionsPressedTextColor, 0)
            recycle()
        }
    }

    fun setOnMergeClickListener(listener: OnClickListener) {
        binding.merge.setOnClickListener(listener)
    }

    fun setOnDeclineClickListener(listener: OnClickListener) {
        binding.decline.setOnClickListener(listener)
    }

    fun showActions(pullRequest: PullRequest, account: User) {
        val participant = pullRequest.findParticipantByUuid(account.uuid)
        if (participant != null) {
            when (participant.state) {
                ParticipantState.APPROVED -> {
                    showApprovedState()
                }
                ParticipantState.CHANGES_REQUESTED -> {
                    showDefaultApproveState()
                }
                else -> showDefaultButtonState()
            }
        } else {
            showDefaultButtonState()
        }

        show()
    }

    private fun showApprovedState() {
        binding.approve.apply {
            setBackgroundTint(approvedBackground)
            onRevokeApproveClickListener?.let { setOnClickListener { it() } }
            setText(R.string.pr_action_unapprove)
            setTextColor(pressedTextColor)
            setDrawableTint(pressedTextColor)
        }
    }

    private fun showDefaultButtonState() {
        showDefaultApproveState()
    }

    private fun showDefaultApproveState() {
        binding.approve.apply {
            setBackgroundTint(defaultBackground)
            onApproveClickListener?.let { setOnClickListener { it() } }
            setText(R.string.pr_action_approve)
            setTextColor(defaultTextColor)
            setDrawableTint(approvedBackground)
        }
    }
}
