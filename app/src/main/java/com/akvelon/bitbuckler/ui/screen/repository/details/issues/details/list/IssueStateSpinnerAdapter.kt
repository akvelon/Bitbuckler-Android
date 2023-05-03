package com.akvelon.bitbuckler.ui.screen.repository.details.issues.details.list

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemIssueStateDropdownBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.repository.issue.IssueState

class IssueStateSpinnerAdapter(private val items: Array<IssueState>) : BaseAdapter() {

    override fun getCount() = items.size

    override fun getItem(position: Int) = items[position]

    override fun getItemId(position: Int) = -1L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding =
            ItemIssueStateDropdownBinding.bind(
                convertView ?: parent.inflate(R.layout.item_issue_state_dropdown)
            )
        with(binding.filter) {
            val issue = items[position]
            text = issue.value.capitalized
            setTextColor(getColor(issue.getStateColor()))
            setBackgroundResource(issue.getStateBackground())
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
