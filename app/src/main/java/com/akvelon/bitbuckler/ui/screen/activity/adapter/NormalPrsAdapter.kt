/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 22 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.activity.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemNormalPrBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequestType
import com.akvelon.bitbuckler.model.entity.pullrequest.TrackedPullRequest
import com.akvelon.bitbuckler.ui.base.BaseViewHolder
import com.akvelon.bitbuckler.ui.screen.activity.adapter.diffUtils.PrsDiffUtilCallback

class NormalPrsAdapter(
    private val clickListener: (TrackedPullRequest) -> Unit,
) : RecyclerView.Adapter<NormalPrsAdapter.NormalPRsViewHolder>() {

    private var items = listOf<TrackedPullRequest>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalPRsViewHolder {
        val view = parent.inflate(R.layout.item_normal_pr)
        return NormalPRsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NormalPRsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun setData(trackedPullRequests: List<TrackedPullRequest>) {
        val diffResult = DiffUtil.calculateDiff(PrsDiffUtilCallback(items, trackedPullRequests))
        items = trackedPullRequests
        diffResult.dispatchUpdatesTo(this)
    }

    inner class NormalPRsViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val binding by viewBinding(ItemNormalPrBinding::bind)

        init {
            itemView.setOnClickListener {
                clickListener(items[bindingAdapterPosition])
            }
        }

        fun bind(trackedPR: TrackedPullRequest) =
            with(binding) {

                avatar.loadCircle(trackedPR.authorAvatar)

                author.text = trackedPR.authorDisplayName
                title.text = trackedPR.title

                updatedOn.text = itemView.getString(
                    R.string.updated,
                    trackedPR.updatedOn.timeAgo(itemView.context)
                )

                when (trackedPR.commentCount) {
                    0 -> commentCount.hide()
                    else -> {
                        commentCount.text = trackedPR.commentCount.toString()
                        commentCount.show()
                    }
                }

                state.apply {
                    text = trackedPR.state.toString()
                    setTextColor(itemView.getColor(trackedPR.state.getStateTextColor()))
                    setBackgroundResource(trackedPR.state.getStateBackground())
                }

                ivReviewer.isVisible = trackedPR.type == PullRequestType.I_AM_REVIEWING

            }
    }
}
