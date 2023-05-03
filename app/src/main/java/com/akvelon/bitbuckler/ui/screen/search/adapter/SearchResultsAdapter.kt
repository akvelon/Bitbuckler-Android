/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Alexander Vinogradov (a.vinogradov@akvelon.com)
 * on 07 December 2021
 */

package com.akvelon.bitbuckler.ui.screen.search.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemProgressErrorBinding
import com.akvelon.bitbuckler.databinding.ItemSearchResultBinding
import com.akvelon.bitbuckler.extension.addEndlessOnScrollListener
import com.akvelon.bitbuckler.extension.getString
import com.akvelon.bitbuckler.extension.inflate
import com.akvelon.bitbuckler.model.entity.search.CodeSearchResult
import com.akvelon.bitbuckler.model.entity.search.SearchSegment
import com.akvelon.bitbuckler.ui.base.BaseViewHolder
import com.akvelon.bitbuckler.ui.pagination.ProgressErrorItem
import com.akvelon.bitbuckler.ui.pagination.ProgressItem

class SearchResultsAdapter(
    private val onResultClickListener: (CodeSearchResult) -> Unit,
    private val onResultRepositoryClickListener: (CodeSearchResult) -> Unit,
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
            is CodeSearchResult -> ITEM_VIEW_TYPE
            is ProgressItem -> PROGRESS_VIEW_TYPE
            else -> PROGRESS_ERROR_VIEW_TYPE
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            ITEM_VIEW_TYPE -> SearchResultHolder(parent.inflate(R.layout.item_search_result))
            PROGRESS_VIEW_TYPE -> ProgressItemHolder(parent.inflate(R.layout.item_progress))
            else -> ProgressErrorItemHolder(parent.inflate(R.layout.item_progress_error))
        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is SearchResultHolder) {
            holder.bind(items[position] as CodeSearchResult)
        } else if (holder is ProgressErrorItemHolder) {
            holder.bind()
        }
    }

    override fun getItemCount() = items.size

    fun setData(codeSearchResults: List<CodeSearchResult>) {
        val diffResult = DiffUtil.calculateDiff(
            SearchResultsDiffUtilCallback(items, codeSearchResults)
        )

        items.apply {
            clear()
            addAll(codeSearchResults)
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

    inner class SearchResultHolder(itemView: View) : BaseViewHolder(itemView) {
        private val binding by viewBinding(ItemSearchResultBinding::bind)

        init {
            itemView.setOnClickListener {
                onResultClickListener(items[bindingAdapterPosition] as CodeSearchResult)
            }
        }

        fun bind(codeSearchResult: CodeSearchResult) {
            val result = codeSearchResult.copyWithMergedContentMatches()
            with(binding) {
                repository.text = root.getString(R.string.workspace_and_repo).format(
                    result.file.getWorkspace(),
                    result.file.getRepo(),
                )
                repository.setOnClickListener { onResultRepositoryClickListener(codeSearchResult) }
                pathMatches.adapter = SegmentsAdapter(
                    { onResultClickListener(codeSearchResult) },
                    if (result.pathMatches.isNotEmpty()) {
                        result.pathMatches
                    } else {
                        listOf(SearchSegment(result.file.path, false))
                    }
                )
                searchResultContent.adapter = ContentMatchesAdapter(
                    { onResultClickListener(codeSearchResult) },
                    result.contentMatches
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
