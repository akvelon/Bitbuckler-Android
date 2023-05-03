/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Viacheslav Loktistov (v.loktistov@akvelon.com)
 * on 29 November 2021
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.commits.adapter

import androidx.recyclerview.widget.DiffUtil
import com.akvelon.bitbuckler.model.entity.repository.Commit

class CommitDiffUtilCallback(
    private val items: List<Any>,
    private val newCommits: List<Commit>
) : DiffUtil.Callback() {

    override fun getOldListSize() = items.size

    override fun getNewListSize() = newCommits.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        when {
            items[oldItemPosition] is Commit ->
                (items[oldItemPosition] as Commit).hash == newCommits[newItemPosition].hash
            else -> false
        }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        items[oldItemPosition] == newCommits[newItemPosition]
}
