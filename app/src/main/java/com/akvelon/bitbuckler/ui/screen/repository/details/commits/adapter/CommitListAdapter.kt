/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 13 May 2021
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.commits.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemCommitBinding
import com.akvelon.bitbuckler.databinding.ItemProgressErrorBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.repository.Commit
import com.akvelon.bitbuckler.ui.base.BaseViewHolder
import com.akvelon.bitbuckler.ui.pagination.ProgressErrorItem
import com.akvelon.bitbuckler.ui.pagination.ProgressItem

class CommitListAdapter(
    private val commitClickListener: (Commit) -> Unit,
    private val loadNextPage: () -> Unit
) : RecyclerView.Adapter<BaseViewHolder>() {

    companion object {
        const val ITEM_VIEW_TYPE = 0
        const val PROGRESS_VIEW_TYPE = 1
        const val PROGRESS_ERROR_VIEW_TYPE = 2

        private const val COMMIT_HASH_LENGTH = 7
    }

    private val items = mutableListOf<Any>()

    private lateinit var recyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        this.recyclerView = recyclerView
    }

    override fun getItemViewType(position: Int) =
        when (items[position]) {
            is Commit -> ITEM_VIEW_TYPE
            is ProgressItem -> PROGRESS_VIEW_TYPE
            else -> PROGRESS_ERROR_VIEW_TYPE
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            ITEM_VIEW_TYPE -> CommitHolder(parent.inflate(R.layout.item_commit))
            PROGRESS_VIEW_TYPE -> ProgressItemHolder(parent.inflate(R.layout.item_progress))
            else -> ProgressErrorItemHolder(parent.inflate(R.layout.item_progress_error))
        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is CommitHolder -> holder.bind(items[position] as Commit)
            is ProgressErrorItemHolder -> holder.bind()
        }
    }

    override fun getItemCount() = items.size

    fun setData(commits: List<Commit>) {
        val diffResult = DiffUtil.calculateDiff(
            CommitDiffUtilCallback(items, commits)
        )
        items.apply {
            clear()
            addAll(commits)
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

    inner class CommitHolder(itemView: View) : BaseViewHolder(itemView) {
        private val binding by viewBinding(ItemCommitBinding::bind)

        init {
            itemView.setOnClickListener {
                commitClickListener(items[bindingAdapterPosition] as Commit)
            }
        }

        fun bind(commit: Commit) = with(binding) {
            commit.author.user?.let {
                avatar.loadCircle(it.links.avatar.href)
                author.text = it.displayName
            } ?: run {
                avatar.setImageResource(R.drawable.ic_avatar_placeholder)
                author.text = commit.author.raw
            }

            message.text = commit.message.replace('\n', ' ')

            commitHash.text = commit.hash.take(COMMIT_HASH_LENGTH)
            createdOn.text = itemView.getString(
                R.string.created,
                commit.date.timeAgo(itemView.context)
            )
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
