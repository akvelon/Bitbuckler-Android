/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Viacheslav Loktistov (v.loktistov@akvelon.com)
 * on 29 November 2021
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.pullrequests.adapter

import androidx.recyclerview.widget.DiffUtil
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequest

class PullRequestDiffUtilCallback(
    private val items: List<Any>,
    private val newPullRequests: List<PullRequest>
) : DiffUtil.Callback() {

    override fun getOldListSize() = items.size

    override fun getNewListSize() = newPullRequests.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = items[oldItemPosition]
        val newItem = newPullRequests[newItemPosition]
        return when (oldItem) {
            is PullRequest -> oldItem.id == newItem.id
            else -> false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        items[oldItemPosition] == newPullRequests[newItemPosition]
}
