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
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemUserPrBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequestType
import com.akvelon.bitbuckler.model.entity.pullrequest.TrackedPullRequest
import com.akvelon.bitbuckler.ui.base.BaseViewHolder
import com.akvelon.bitbuckler.ui.screen.activity.adapter.diffUtils.PrsDiffUtilCallback

class UserPrsAdapter(
    private val clickListener: (TrackedPullRequest) -> Unit,
    private val windowWidth: Int,
) : RecyclerView.Adapter<UserPrsAdapter.UserPRsViewHolder>() {

    private val items = mutableListOf<TrackedPullRequest>()

    private lateinit var recyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPRsViewHolder {
        val view = parent.inflate(R.layout.item_user_pr)
        view.updateLayoutParams {
            width = (windowWidth * 0.75f).toInt()
        }
        return UserPRsViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserPRsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun setData(trackedPullRequests: List<TrackedPullRequest>) {
        val diffResult = DiffUtil.calculateDiff(PrsDiffUtilCallback(items, trackedPullRequests))
        items.clear()
        items.addAll(trackedPullRequests)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class UserPRsViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val binding by viewBinding(ItemUserPrBinding::bind)

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

                state.apply {
                    text = trackedPR.state.toString()
                    setTextColor(itemView.getColor(trackedPR.state.getStateTextColor()))
                    setBackgroundResource(trackedPR.state.getStateBackground())
                }

                commentCount.isVisible = trackedPR.commentCount > 0

                commentIcon.isVisible = trackedPR.commentCount > 0

                commentCount.text = trackedPR.commentCount.toString()

            }
    }
}
