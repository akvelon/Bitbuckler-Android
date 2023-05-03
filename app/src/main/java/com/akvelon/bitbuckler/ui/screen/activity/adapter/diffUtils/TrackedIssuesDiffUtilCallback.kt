/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Viacheslav Loktistov (v.loktistov@akvelon.com)
 * on 29 November 2021
 */

package com.akvelon.bitbuckler.ui.screen.activity.adapter.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.akvelon.bitbuckler.model.entity.issueTracker.TrackedIssue
import com.akvelon.bitbuckler.model.entity.repository.issue.Issue

class TrackedIssuesDiffUtilCallback(
    private val items: List<TrackedIssue>,
    private val newItems: List<TrackedIssue>,
) : DiffUtil.Callback() {

    override fun getOldListSize() = items.size

    override fun getNewListSize() = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = items[oldItemPosition]
        val newItem = newItems[newItemPosition]
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        items[oldItemPosition].title == newItems[newItemPosition].title &&
                items[oldItemPosition].assignee?.uuid == newItems[newItemPosition].assignee?.uuid &&
                items[oldItemPosition].reporter.uuid == newItems[newItemPosition].reporter.uuid &&
                items[oldItemPosition].updatedOn == newItems[newItemPosition].updatedOn &&
                items[oldItemPosition].state == newItems[newItemPosition].state &&
                items[oldItemPosition].kind == newItems[newItemPosition].kind &&
                items[oldItemPosition].priority == newItems[newItemPosition].priority
}
