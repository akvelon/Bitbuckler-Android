/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 03 February 2021
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.branches.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemBranchBinding
import com.akvelon.bitbuckler.databinding.ItemProgressErrorBinding
import com.akvelon.bitbuckler.extension.addEndlessOnScrollListener
import com.akvelon.bitbuckler.extension.getString
import com.akvelon.bitbuckler.extension.inflate
import com.akvelon.bitbuckler.extension.timeAgo
import com.akvelon.bitbuckler.model.entity.repository.Branch
import com.akvelon.bitbuckler.ui.base.BaseViewHolder
import com.akvelon.bitbuckler.ui.pagination.ProgressErrorItem
import com.akvelon.bitbuckler.ui.pagination.ProgressItem

class BranchListAdapter(
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
            is Branch -> ITEM_VIEW_TYPE
            is ProgressItem -> PROGRESS_VIEW_TYPE
            else -> PROGRESS_ERROR_VIEW_TYPE
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        when (viewType) {
            ITEM_VIEW_TYPE -> BranchHolder(parent.inflate(R.layout.item_branch))
            PROGRESS_VIEW_TYPE -> ProgressItemHolder(parent.inflate(R.layout.item_progress))
            else -> ProgressErrorItemHolder(parent.inflate(R.layout.item_progress_error))
        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is BranchHolder) {
            holder.bind(items[position] as Branch)
        } else if (holder is ProgressErrorItemHolder) {
            holder.bind()
        }
    }

    override fun getItemCount() = items.size

    fun setData(branches: List<Branch>) {
        val diffResult = DiffUtil.calculateDiff(
            BranchDiffUtilCallback(items, branches)
        )
        items.apply {
            items.clear()
            items.addAll(branches)
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

    inner class BranchHolder(itemView: View) : BaseViewHolder(itemView) {
        private val binding by viewBinding(ItemBranchBinding::bind)

        fun bind(branch: Branch) =
            with(binding) {
                name.text = branch.name

                branch.target?.let {
                    updatedOn.text = itemView.getString(
                        R.string.updated,
                        it.updatedOn.timeAgo(itemView.context)
                    )
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
