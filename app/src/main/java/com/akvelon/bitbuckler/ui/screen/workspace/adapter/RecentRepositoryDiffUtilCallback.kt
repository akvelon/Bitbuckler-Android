/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 15 March 2021
 */

package com.akvelon.bitbuckler.ui.screen.workspace.adapter

import androidx.recyclerview.widget.DiffUtil
import com.akvelon.bitbuckler.model.entity.repository.RecentRepository

class RecentRepositoryDiffUtilCallback(
    private val items: List<Any>,
    private val newRepositories: List<RecentRepository>
) : DiffUtil.Callback() {

    override fun getOldListSize() = items.size

    override fun getNewListSize() = newRepositories.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = items[oldItemPosition]
        val newItem = newRepositories[newItemPosition]
        return when (oldItem) {
            is RecentRepository -> oldItem.uuid == newItem.uuid
            else -> false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        items[oldItemPosition] == newRepositories[newItemPosition]
}
