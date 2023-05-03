package com.akvelon.bitbuckler.ui.screen.repository.details.issues.details.list

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemIssuePriorityDropdownBinding
import com.akvelon.bitbuckler.extension.capitalized
import com.akvelon.bitbuckler.extension.getPriorityIcon
import com.akvelon.bitbuckler.extension.inflate
import com.akvelon.bitbuckler.model.entity.repository.issue.IssuePriority

class IssuePrioritySpinnerAdapter(private val items: Array<IssuePriority>) : BaseAdapter() {

    override fun getCount() = items.size

    override fun getItem(position: Int) = items[position]

    override fun getItemId(position: Int) = -1L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding =
            ItemIssuePriorityDropdownBinding.bind(
                convertView ?: parent.inflate(R.layout.item_issue_priority_dropdown)
            )
        with(binding) {
            val issue = items[position]
            ivType.setImageResource(issue.getPriorityIcon())
            tvType.text = issue.toString().capitalized
        }
        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return super.getDropDownView(position, convertView, parent).apply {
            setPadding(
                resources.getDimensionPixelOffset(R.dimen.spacingNormal),
                resources.getDimensionPixelOffset(R.dimen.spacingPreNormal),
                resources.getDimensionPixelOffset(R.dimen.spacingNormal),
                resources.getDimensionPixelOffset(R.dimen.spacingPreNormal)
            )
        }
    }
}
