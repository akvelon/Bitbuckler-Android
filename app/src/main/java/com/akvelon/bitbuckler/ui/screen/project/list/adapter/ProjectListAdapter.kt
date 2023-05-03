/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 09 December 2020
 */

package com.akvelon.bitbuckler.ui.screen.project.list.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemProgressErrorBinding
import com.akvelon.bitbuckler.databinding.ItemProjectBinding
import com.akvelon.bitbuckler.extension.addEndlessOnScrollListener
import com.akvelon.bitbuckler.extension.inflate
import com.akvelon.bitbuckler.extension.loadCircle
import com.akvelon.bitbuckler.extension.show
import com.akvelon.bitbuckler.model.entity.Project
import com.akvelon.bitbuckler.model.entity.Workspace
import com.akvelon.bitbuckler.ui.base.BaseViewHolder
import com.akvelon.bitbuckler.ui.pagination.ProgressErrorItem
import com.akvelon.bitbuckler.ui.pagination.ProgressItem

class ProjectListAdapter(
    private val clickListener: (Project) -> Unit,
    private val loadNextPage: () -> Unit,
    private val workspace: Workspace
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
            is Project -> ITEM_VIEW_TYPE
            is ProgressItem -> PROGRESS_VIEW_TYPE
            else -> PROGRESS_ERROR_VIEW_TYPE
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            ITEM_VIEW_TYPE -> ProjectHolder(parent.inflate(R.layout.item_project))
            PROGRESS_VIEW_TYPE -> ProgressItemHolder(parent.inflate(R.layout.item_progress))
            else -> ProgressErrorItemHolder(parent.inflate(R.layout.item_progress_error))
        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is ProjectHolder) {
            holder.bind(items[position] as Project)
        } else if (holder is ProgressErrorItemHolder) {
            holder.bind()
        }
    }

    override fun getItemCount() = items.size

    fun setData(projects: List<Project>) {
        val diffResult = DiffUtil.calculateDiff(
            ProjectDiffUtilCallback(items, projects)
        )

        items.apply {
            items.clear()
            items.addAll(projects)
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

    inner class ProjectHolder(itemView: View) : BaseViewHolder(itemView) {
        private val binding by viewBinding(ItemProjectBinding::bind)

        init {
            itemView.setOnClickListener {
                clickListener(items[bindingAdapterPosition] as Project)
            }
        }

        fun bind(project: Project) =
            with(binding) {
                name.text = project.name
                workspaceName.text = workspace.name
                avatar.loadCircle(project.links.avatar.href, R.drawable.ic_project)

                if (project.isPrivate == true) {
                    privacy.show()
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
