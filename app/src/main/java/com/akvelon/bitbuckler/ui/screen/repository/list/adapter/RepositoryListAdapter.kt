/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 14 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.repository.list.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemProgressErrorBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.repository.Repository
import com.akvelon.bitbuckler.ui.base.BaseViewHolder
import com.akvelon.bitbuckler.ui.pagination.ProgressErrorItem
import com.akvelon.bitbuckler.ui.pagination.ProgressItem
import com.akvelon.bitbuckler.ui.viewholders.ProgressItemHolder
import com.akvelon.bitbuckler.ui.viewholders.RepositoryHolder

class RepositoryListAdapter(
    private val clickListener: (Repository) -> Unit,
    private val setTrackListener: (Repository, Boolean) -> Unit,
    private val loadNextPage: () -> Unit = {},
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
            is Repository -> ITEM_VIEW_TYPE
            is ProgressItem -> PROGRESS_VIEW_TYPE
            else -> PROGRESS_ERROR_VIEW_TYPE
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            ITEM_VIEW_TYPE -> RepositoryHolder(
                parent.inflate(R.layout.item_repository),
                clickListener
            ) { repos, isTracked ->
                (items.first { repos == it } as? Repository)?.isTracked = isTracked
                setTrackListener(repos, isTracked)
            }
            PROGRESS_VIEW_TYPE -> ProgressItemHolder(parent.inflate(R.layout.item_progress))
            else -> ProgressErrorItemHolder(parent.inflate(R.layout.item_progress_error))
        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is RepositoryHolder) {
            holder.bind(items[position] as Repository)
        } else if (holder is ProgressErrorItemHolder) {
            holder.bind()
        }
    }

    override fun getItemCount() = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(repositories: List<Repository>) {
        val diffResult = DiffUtil.calculateDiff(
            RepositoryDiffUtilCallback(items, repositories)
        )

        items.apply {
            clear()
            notifyDataSetChanged()
            addAll(repositories)
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

    inner class ProgressErrorItemHolder(itemView: View) : BaseViewHolder(itemView) {
        private val binding by viewBinding(ItemProgressErrorBinding::bind)

        fun bind() {
            binding.reload.setOnClickListener { loadNextPage() }
        }
    }
}