package com.akvelon.bitbuckler.ui.screen.activity.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemTrackedIssueBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.issueTracker.TrackedIssue
import com.akvelon.bitbuckler.ui.screen.activity.adapter.diffUtils.TrackedIssuesDiffUtilCallback

class TrackedIssuesAdapter(private val clickListener: (TrackedIssue) -> Unit) :
    RecyclerView.Adapter<TrackedIssuesAdapter.TrackedIssuesViewHolder>() {

    private var data = emptyList<TrackedIssue>()

    fun setData(newData: List<TrackedIssue>) {
        val diffResult = DiffUtil.calculateDiff(TrackedIssuesDiffUtilCallback(data, newData))
        data = newData
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackedIssuesViewHolder {
        val view = parent.inflate(R.layout.item_tracked_issue)
        return TrackedIssuesViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackedIssuesViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class TrackedIssuesViewHolder(inflate: View) : RecyclerView.ViewHolder(inflate) {

        private val binding by viewBinding(ItemTrackedIssueBinding::bind)

        fun bind(issue: TrackedIssue) {
            with(binding) {

                root.setOnClickListener {
                    clickListener.invoke(issue)
                }

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

                createdOn.text = itemView.getString(R.string.created, issue.createdOn.timeAgoShort(itemView.context))

                updatedOn.text = itemView.getString(R.string.updated, issue.updatedOn.timeAgoShort(itemView.context))

                tvIssueCode.text = itemView.getString(R.string.number_sign, issue.id)

                tvIssueTitle.text = issue.title

                ivPriority.setImageResource(issue.priority.getPriorityIcon())

                tvPriority.text = issue.priority.toString().capitalized

                ivType.setImageResource(issue.kind.getKindIcon())

                tvType.text = issue.kind.toString().capitalized

                commentCount.isVisible = issue.commentCount > 0

                commentIcon.isVisible = issue.commentCount > 0

                commentCount.text = issue.commentCount.toString()

            }
        }
    }
}
