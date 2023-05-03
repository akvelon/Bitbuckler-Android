/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Viacheslav Loktistov (v.loktistov@akvelon.com)
 * on 29 November 2021
 */

package com.akvelon.bitbuckler.ui.screen.project.list.adapter

import androidx.recyclerview.widget.DiffUtil
import com.akvelon.bitbuckler.model.entity.Project

class ProjectDiffUtilCallback(
    private val items: List<Any>,
    private val newProjects: List<Project>
) : DiffUtil.Callback() {

    override fun getOldListSize() = items.size

    override fun getNewListSize() = newProjects.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = items[oldItemPosition]
        val newItem = newProjects[newItemPosition]
        return when (oldItem) {
            is Project -> oldItem.uuid == newItem.uuid
            else -> false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        items[oldItemPosition] == newProjects[newItemPosition]
}
