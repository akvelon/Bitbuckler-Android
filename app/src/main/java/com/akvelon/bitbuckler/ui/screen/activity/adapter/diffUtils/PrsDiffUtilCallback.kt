/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Viacheslav Loktistov (v.loktistov@akvelon.com)
 * on 29 November 2021
 */

package com.akvelon.bitbuckler.ui.screen.activity.adapter.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.akvelon.bitbuckler.model.entity.pullrequest.TrackedPullRequest

class PrsDiffUtilCallback(
    private val items: List<TrackedPullRequest>,
    private val newTrackedPullRequest: List<TrackedPullRequest>
) : DiffUtil.Callback() {

    override fun getOldListSize() = items.size

    override fun getNewListSize() = newTrackedPullRequest.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = items[oldItemPosition]
        val newItem = newTrackedPullRequest[newItemPosition]
        return oldItem.idInDB == newItem.idInDB || oldItem.idInRepository == newItem.idInRepository
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        items[oldItemPosition].title == newTrackedPullRequest[newItemPosition].title &&
                items[oldItemPosition].authorDisplayName == newTrackedPullRequest[newItemPosition].authorDisplayName &&
                items[oldItemPosition].authorAvatar == newTrackedPullRequest[newItemPosition].authorAvatar &&
                items[oldItemPosition].updatedOn == newTrackedPullRequest[newItemPosition].updatedOn &&
                items[oldItemPosition].state == newTrackedPullRequest[newItemPosition].state &&
                items[oldItemPosition].commentCount == newTrackedPullRequest[newItemPosition].commentCount &&
                items[oldItemPosition].type == newTrackedPullRequest[newItemPosition].type

}
