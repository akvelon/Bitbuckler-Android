/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Viacheslav Loktistov (v.loktistov@akvelon.com)
 * on 29 November 2021
 */

package com.akvelon.bitbuckler.ui.screen.repository.list.adapter

import androidx.recyclerview.widget.DiffUtil
import com.akvelon.bitbuckler.model.entity.repository.Repository

class RepositoryDiffUtilCallback(
    private val items: List<Any>,
    private val newRepositories: List<Repository>
) : DiffUtil.Callback() {

    override fun getOldListSize() = items.size

    override fun getNewListSize() = newRepositories.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = items[oldItemPosition]
        val newItem = newRepositories[newItemPosition]
        return when (oldItem) {
            is Repository -> oldItem.uuid == newItem.uuid
            else -> false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        items[oldItemPosition] == newRepositories[newItemPosition]
}
