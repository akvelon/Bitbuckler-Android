/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Viacheslav Loktistov (v.loktistov@akvelon.com)
 * on 29 November 2021
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.source.list.adapter

import androidx.recyclerview.widget.DiffUtil
import com.akvelon.bitbuckler.model.entity.source.Source

class SourceDiffUtilCallback(
    private val items: List<Any>,
    private val newSources: List<Any>
) : DiffUtil.Callback() {

    override fun getOldListSize() = items.size

    override fun getNewListSize() = newSources.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = items[oldItemPosition]
        val newItem = newSources[newItemPosition]
        return when {
            oldItem is Source && newItem is Source -> oldItem.path == newItem.path
            oldItem is PreviousFolderItem && newItem is PreviousFolderItem -> true
            else -> false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        items[oldItemPosition] == newSources[newItemPosition]
}
