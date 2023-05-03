package com.akvelon.bitbuckler.ui.screen.activity.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemUserIssueBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.issueTracker.TrackedIssue
import com.akvelon.bitbuckler.ui.screen.activity.adapter.diffUtils.TrackedIssuesDiffUtilCallback

class UserIssuesAdapter(
    private val windowWidth: Int,
    private val clickListener: (TrackedIssue) -> Unit,
) : RecyclerView.Adapter<UserIssuesAdapter.UserIssuesViewHolder>() {

    private var data = emptyList<TrackedIssue>()

    fun setData(newData: List<TrackedIssue>) {
        val diffResult = DiffUtil.calculateDiff(TrackedIssuesDiffUtilCallback(data, newData))
        data = newData
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserIssuesViewHolder {
        val view = parent.inflate(R.layout.item_user_issue)
        view.updateLayoutParams {
            width = (windowWidth * 0.55f).toInt()
        }
        return UserIssuesViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserIssuesViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class UserIssuesViewHolder(inflate: View) : RecyclerView.ViewHolder(inflate) {

        private val binding by viewBinding(ItemUserIssueBinding::bind)

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

                createdOn.text = itemView.getString(
                    R.string.created,
                    issue.createdOn.timeAgoShort(itemView.context)
                )

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
