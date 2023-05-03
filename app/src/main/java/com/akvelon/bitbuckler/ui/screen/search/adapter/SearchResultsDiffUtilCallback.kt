/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Alexander Vinogradov (a.vinogradov@akvelon.com)
 * on 27 December 2021
 */

package com.akvelon.bitbuckler.ui.screen.search.adapter

import androidx.recyclerview.widget.DiffUtil
import com.akvelon.bitbuckler.model.entity.search.CodeSearchResult

class SearchResultsDiffUtilCallback(
    private val items: List<Any>,
    private val newSearchResults: List<CodeSearchResult>
) : DiffUtil.Callback() {

    override fun getOldListSize() = items.size

    override fun getNewListSize() = newSearchResults.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = items[oldItemPosition]
        val newItem = newSearchResults[newItemPosition]
        return when (oldItem) {
            is CodeSearchResult -> oldItem.contentMatches == newItem.contentMatches
            else -> false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        items[oldItemPosition] == newSearchResults[newItemPosition]
}
