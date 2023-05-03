/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 16 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.pullrequests.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemProgressErrorBinding
import com.akvelon.bitbuckler.databinding.ItemPullRequestBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequest
import com.akvelon.bitbuckler.ui.base.BaseViewHolder
import com.akvelon.bitbuckler.ui.pagination.ProgressErrorItem
import com.akvelon.bitbuckler.ui.pagination.ProgressItem

class PullRequestListAdapter(
    private val clickListener: (PullRequest) -> Unit,
    private val loadNextPage: () -> Unit
) : RecyclerView.Adapter<BaseViewHolder>() {

    companion object {
        const val ITEM_VIEW_TYPE = 0
        const val PROGRESS_VIEW_TYPE = 1
        const val PROGRESS_ERROR_VIEW_TYPE = 2
    }

    private val items = mutableListOf<Any>()

    private lateinit var recyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        this.recyclerView = recyclerView
    }

    override fun getItemViewType(position: Int) =
        when (items[position]) {
            is PullRequest -> ITEM_VIEW_TYPE
            is ProgressItem -> PROGRESS_VIEW_TYPE
            else -> PROGRESS_ERROR_VIEW_TYPE
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        when (viewType) {
            ITEM_VIEW_TYPE -> PullRequestHolder(parent.inflate(R.layout.item_pull_request))
            PROGRESS_VIEW_TYPE -> ProgressItemHolder(parent.inflate(R.layout.item_progress))
            else -> ProgressErrorItemHolder(parent.inflate(R.layout.item_progress_error))
        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is PullRequestHolder) {
            holder.bind(items[position] as PullRequest)
        } else if (holder is ProgressErrorItemHolder) {
            holder.bind()
        }
    }

    override fun getItemCount() = items.size

    fun setData(pullRequests: List<PullRequest>) {
        val diffResult = DiffUtil.calculateDiff(
            PullRequestDiffUtilCallback(items, pullRequests)
        )

        items.apply {
            clear()
            addAll(pullRequests)
        }
        diffResult.dispatchUpdatesTo(this)
    }

    fun showProgress(show: Boolean) {
        if (show && !isProgress()) {
            items.add(ProgressItem())
            notifyItemInserted(items.lastIndex)
        } else if (!show && isProgress()) {
            items.removeLast()
            notifyItemRemoved(items.lastIndex + 1)
        }
    }

    fun showProgressError(show: Boolean) {
        if (show && !isProgressError()) {
            items.add(ProgressErrorItem())
            recyclerView.clearOnScrollListeners()
            notifyItemInserted(items.lastIndex)
        } else if (!show && isProgressError()) {
            items.removeLast()
            recyclerView.addEndlessOnScrollListener { loadNextPage() }
            notifyItemRemoved(items.lastIndex + 1)
        }
    }

    private fun isProgress() = items.isNotEmpty() && items.last() is ProgressItem

    private fun isProgressError() = items.isNotEmpty() && items.last() is ProgressErrorItem

    inner class PullRequestHolder(itemView: View) : BaseViewHolder(itemView) {
        private val binding by viewBinding(ItemPullRequestBinding::bind)

        init {
            itemView.setOnClickListener {
                clickListener(items[bindingAdapterPosition] as PullRequest)
            }
        }

        fun bind(pr: PullRequest) =
            with(binding) {
                with(pr.author) {
                    avatar.loadCircle(links.avatar.href)
                    author.text = displayName
                }

                title.text = pr.title

                createdOn.text = itemView.getString(
                    R.string.created,
                    pr.createdOn.timeAgo(itemView.context)
                )
                updatedOn.text = itemView.getString(
                    R.string.updated,
                    pr.updatedOn.timeAgo(itemView.context)
                )

                requestId.text = itemView.getString(R.string.pull_request_id, pr.id)
                iconState.setTint(pr.state.getAccentColor())

                when (pr.commentCount) {
                    0 -> commentCount.hide()
                    else -> {
                        commentCount.show()
                        commentCount.text = pr.commentCount.toString()
                    }
                }
            }
    }

    inner class ProgressItemHolder(itemView: View) : BaseViewHolder(itemView)

    inner class ProgressErrorItemHolder(itemView: View) : BaseViewHolder(itemView) {
        private val binding by viewBinding(ItemProgressErrorBinding::bind)

        fun bind() {
            binding.reload.setOnClickListener { loadNextPage() }
        }
    }
}
