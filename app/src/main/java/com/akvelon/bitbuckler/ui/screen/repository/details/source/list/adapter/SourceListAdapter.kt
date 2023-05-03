/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 04 May 2021
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.source.list.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemProgressErrorBinding
import com.akvelon.bitbuckler.databinding.ItemSourceBackBinding
import com.akvelon.bitbuckler.databinding.ItemSourceFileBinding
import com.akvelon.bitbuckler.extension.addEndlessOnScrollListener
import com.akvelon.bitbuckler.extension.getIcon
import com.akvelon.bitbuckler.extension.inflate
import com.akvelon.bitbuckler.extension.isRoot
import com.akvelon.bitbuckler.model.entity.source.Source
import com.akvelon.bitbuckler.ui.base.BaseViewHolder
import com.akvelon.bitbuckler.ui.pagination.ProgressErrorItem
import com.akvelon.bitbuckler.ui.pagination.ProgressItem

class SourceListAdapter(
    private val itemClickListener: (Source) -> Unit,
    private val toPreviousFolderClickListener: () -> Unit,
    private val loadNextPage: () -> Unit
) : RecyclerView.Adapter<BaseViewHolder>() {

    companion object {
        const val ITEM_VIEW_TYPE = 0
        const val PREVIOUS_FOLDER_VIEW_TYPE = 1
        const val PROGRESS_VIEW_TYPE = 2
        const val PROGRESS_ERROR_VIEW_TYPE = 3

        const val MIN_ITEM_COUNT = 20
    }

    private val items = mutableListOf<Any>()

    private lateinit var recyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        this.recyclerView = recyclerView
    }

    override fun getItemViewType(position: Int) =
        when (items[position]) {
            is Source -> ITEM_VIEW_TYPE
            is PreviousFolderItem -> PREVIOUS_FOLDER_VIEW_TYPE
            is ProgressItem -> PROGRESS_VIEW_TYPE
            else -> PROGRESS_ERROR_VIEW_TYPE
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            ITEM_VIEW_TYPE -> SourceHolder(parent.inflate(R.layout.item_source_file))
            PREVIOUS_FOLDER_VIEW_TYPE -> PreviousFolderHolder(parent.inflate(R.layout.item_source_back))
            PROGRESS_VIEW_TYPE -> ProgressItemHolder(parent.inflate(R.layout.item_progress))
            else -> ProgressErrorItemHolder(parent.inflate(R.layout.item_progress_error))
        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is SourceHolder -> holder.bind(items[position] as Source)
            is PreviousFolderHolder -> holder.bind()
            is ProgressErrorItemHolder -> holder.bind()
        }
    }

    override fun getItemCount() = items.size

    fun setData(sources: List<Source>) {
        val newItems = mutableListOf<Any>().apply {
            if (!sources.isRoot()) {
                add(PreviousFolderItem())
            }
            addAll(sources)
        }

        val diffResult = DiffUtil.calculateDiff(
            SourceDiffUtilCallback(items, newItems)
        )

        items.apply {
            clear()
            addAll(newItems)
        }
        diffResult.dispatchUpdatesTo(this)

        /**
         * It's a temporary solution for resolve problem with non-scrolled lists
         */
        if (items.size < MIN_ITEM_COUNT) {
            loadNextPage()
        }
    }

    fun showProgress(show: Boolean) {
        if (show && !isProgress()) {
            items.add(ProgressItem())
            notifyItemInserted(items.size - 1)
        } else if (!show && isProgress()) {
            items.removeLast()
            notifyItemRemoved(items.size)
        }
    }

    fun showProgressError(show: Boolean) {
        if (show && !isProgressError()) {
            items.add(ProgressErrorItem())
            recyclerView.clearOnScrollListeners()
            notifyItemInserted(items.size - 1)
        } else if (!show && isProgressError()) {
            items.removeLast()
            recyclerView.addEndlessOnScrollListener { loadNextPage() }
            notifyItemRemoved(items.size)
        }
    }

    private fun isProgress() = items.isNotEmpty() && items.last() is ProgressItem

    private fun isProgressError() = items.isNotEmpty() && items.last() is ProgressErrorItem

    inner class SourceHolder(itemView: View) : BaseViewHolder(itemView) {
        private val binding by viewBinding(ItemSourceFileBinding::bind)

        init {
            itemView.setOnClickListener {
                itemClickListener(items[bindingAdapterPosition] as Source)
            }
        }

        fun bind(source: Source) = with(binding) {
            icon.setImageResource(source.getIcon())
            filename.text = source.name
        }
    }

    inner class PreviousFolderHolder(itemView: View) : BaseViewHolder(itemView) {
        private val binding by viewBinding(ItemSourceBackBinding::bind)

        fun bind() {
            binding.root.setOnClickListener { toPreviousFolderClickListener() }
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
