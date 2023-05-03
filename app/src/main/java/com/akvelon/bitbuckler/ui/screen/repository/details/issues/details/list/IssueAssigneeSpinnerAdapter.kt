package com.akvelon.bitbuckler.ui.screen.repository.details.issues.details.list

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemIssueAssigneeDropdownBinding
import com.akvelon.bitbuckler.extension.getString
import com.akvelon.bitbuckler.extension.inflate
import com.akvelon.bitbuckler.extension.loadCircle
import com.akvelon.bitbuckler.model.entity.User

class IssueAssigneeSpinnerAdapter(private val items: List<User?>) : BaseAdapter() {

    override fun getCount() = items.size

    override fun getItem(position: Int) = items[position]

    override fun getItemId(position: Int) = -1L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemIssueAssigneeDropdownBinding.bind(convertView ?: parent.inflate(R.layout.item_issue_assignee_dropdown))
        with(binding) {
            val user = items[position]
            if (user == null) {
                ivAssignee.setImageResource(R.drawable.ic_avatar_placeholder)
                tvAssigneeName.text = binding.root.getString(R.string.unassigned)
            } else {
                ivAssignee.loadCircle(user.links.avatar.href)
                tvAssigneeName.text = user.displayName
            }
        }
        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return super.getDropDownView(position, convertView, parent).apply {
            setPadding(resources.getDimensionPixelOffset(R.dimen.spacingNormal),
                resources.getDimensionPixelOffset(R.dimen.spacingPreNormal),
                resources.getDimensionPixelOffset(R.dimen.spacingNormal),
                resources.getDimensionPixelOffset(R.dimen.spacingPreNormal))
        }
    }
}
