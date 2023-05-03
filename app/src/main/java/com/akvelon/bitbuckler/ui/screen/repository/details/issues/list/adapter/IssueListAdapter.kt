/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 05 February 2021
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.issues.list.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemIssueBinding
import com.akvelon.bitbuckler.databinding.ItemProgressErrorBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.repository.issue.Issue
import com.akvelon.bitbuckler.ui.base.BaseViewHolder
import com.akvelon.bitbuckler.ui.pagination.ProgressErrorItem
import com.akvelon.bitbuckler.ui.pagination.ProgressItem

class IssueListAdapter(
    private val clickListener: (Issue) -> Unit,
    private val loadNextPage: () -> Unit,
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
            is Issue -> ITEM_VIEW_TYPE
            is ProgressItem -> PROGRESS_VIEW_TYPE
            else -> PROGRESS_ERROR_VIEW_TYPE
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        when (viewType) {
            ITEM_VIEW_TYPE -> IssueHolder(parent.inflate(R.layout.item_issue))
            PROGRESS_VIEW_TYPE -> ProgressItemHolder(parent.inflate(R.layout.item_progress))
            else -> ProgressErrorItemHolder(parent.inflate(R.layout.item_progress_error))
        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is IssueHolder) {
            holder.bind(items[position] as Issue)
        } else if (holder is ProgressErrorItemHolder) {
            holder.bind()
        }
    }

    override fun getItemCount() = items.size

    fun setData(issues: List<Issue>) {
        val diffResult = DiffUtil.calculateDiff(
            IssueDiffUtilCallback(items, issues)
        )

        items.apply {
            clear()
            addAll(issues)
        }
        diffResult.dispatchUpdatesTo(this)
    }

    fun showProgress(show: Boolean) {
        if (show && !isProgress()) {
            items.add(ProgressItem())
            notifyItemInserted(items.lastIndex)
        } else if (!show && isProgress()) {
            items.remove(items.last())
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

    inner class IssueHolder(itemView: View) : BaseViewHolder(itemView) {
        private val binding by viewBinding(ItemIssueBinding::bind)

        init {
            itemView.setOnClickListener {
                clickListener(items[bindingAdapterPosition] as Issue)
            }
        }

        fun bind(issue: Issue) =
            with(binding) {

                with(tvState) {
                    text = issue.state.value.capitalized
                    setTextColor(this.getColor(issue.state.getStateColor()))
                    setBackgroundResource(issue.state.getStateBackground())
                }

                when (issue.assignee) {
                    null -> {
                        ivUserPhoto.hide()
                        tvUserName.hide()
                    }
                    else -> {
                        ivUserPhoto.loadCircle(issue.assignee.links.avatar.href)
                        ivUserPhoto.show()
                        tvUserName.show()
                        tvUserName.text = issue.assignee.displayName
                    }
                }

                createdOn.text = itemView.getString(
                    R.string.created,
                    issue.createdOn.timeAgoShort(itemView.context)
                )

                reporter.text = issue.reporter.displayName

                tvIssueCode.text = itemView.getString(R.string.number_sign, issue.id)

                tvIssueTitle.text = issue.title

                ivPriority.setImageResource(issue.priority.getPriorityIcon())

                tvPriority.text = issue.priority.toString().capitalized

                ivType.setImageResource(issue.kind.getKindIcon())

                tvType.text = issue.kind.toString().capitalized

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
