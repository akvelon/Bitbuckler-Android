/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 18 February 2021
 */

package com.akvelon.bitbuckler.ui.screen.pullrequest.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemReviewerBinding
import com.akvelon.bitbuckler.extension.getStateBorder
import com.akvelon.bitbuckler.extension.inflate
import com.akvelon.bitbuckler.extension.loadCircle
import com.akvelon.bitbuckler.model.entity.participant.Participant
import com.akvelon.bitbuckler.ui.base.BaseViewHolder

class PullRequestReviewersAdapter(
    private val reviewers: List<Participant>,
) : RecyclerView.Adapter<BaseViewHolder>() {

    companion object {
        const val ITEM_VIEW_TYPE = 0
        const val EMPTY_VIEW_TYPE = 1
    }

    override fun getItemViewType(position: Int) =
        if (reviewers.isEmpty()) EMPTY_VIEW_TYPE else ITEM_VIEW_TYPE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == ITEM_VIEW_TYPE) {
            ReviewerHolder(parent.inflate(R.layout.item_reviewer))
        } else {
            EmptyHolder(parent.inflate(R.layout.layout_empty_reviewers))
        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is ReviewerHolder) {
            holder.bind(reviewers[position])
        }
    }

    override fun getItemCount() = if (reviewers.isEmpty()) 1 else reviewers.size

    inner class ReviewerHolder(itemView: View) : BaseViewHolder(itemView) {
        private val binding by viewBinding(ItemReviewerBinding::bind)

        fun bind(reviewer: Participant) =
            with(binding) {
                val mText = with(reviewer.user.displayName.split(" ")) {
                    if(size > 1) {
                        "${get(0)}\n${get(1)}"
                    } else { get(0) }
                }
                name.text = mText
                name.isSelected = true

                avatarLayout.setBackgroundResource(reviewer.getStateBorder())
                avatar.loadCircle(reviewer.user.links.avatar.href)
            }
    }

    inner class EmptyHolder(itemView: View) : BaseViewHolder(itemView)
}
