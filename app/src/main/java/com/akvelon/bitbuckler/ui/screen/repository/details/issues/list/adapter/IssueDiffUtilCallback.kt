/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Viacheslav Loktistov (v.loktistov@akvelon.com)
 * on 29 November 2021
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.issues.list.adapter

import androidx.recyclerview.widget.DiffUtil
import com.akvelon.bitbuckler.model.entity.repository.issue.Issue

class IssueDiffUtilCallback(
    private val items: List<Any>,
    private val newIssues: List<Issue>
) : DiffUtil.Callback() {

    override fun getOldListSize() = items.size

    override fun getNewListSize() = newIssues.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = items[oldItemPosition]
        val newItem = newIssues[newItemPosition]
        return when (oldItem) {
            is Issue -> oldItem.id == newItem.id
            else -> false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        items[oldItemPosition] == newIssues[newItemPosition]
}
