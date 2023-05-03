/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Alexander Vinogradov (a.vinogradov@akvelon.com)
 * on 09 December 2021
 */

package com.akvelon.bitbuckler.ui.screen.search.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemSearchMatchBinding
import com.akvelon.bitbuckler.extension.inflate
import com.akvelon.bitbuckler.model.entity.search.SearchContentMatch
import com.akvelon.bitbuckler.ui.base.BaseViewHolder

class ContentMatchesAdapter(
    private val onResultClickListener: () -> Unit,
    private val searchContentMatches: List<SearchContentMatch>
) : RecyclerView.Adapter<ContentMatchesAdapter.ContentMatchHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ContentMatchHolder(parent.inflate(R.layout.item_search_match))

    override fun onBindViewHolder(holder: ContentMatchHolder, position: Int) {
        holder.bind(searchContentMatches[position])
    }

    override fun getItemCount() = searchContentMatches.size

    inner class ContentMatchHolder(itemView: View) : BaseViewHolder(itemView) {
        private val binding by viewBinding(ItemSearchMatchBinding::bind)

        init {
            itemView.setOnClickListener {
                onResultClickListener()
            }
        }

        fun bind(searchContentMatch: SearchContentMatch) =
            with(binding) {
                lines.adapter = SearchLinesAdapter(
                    { onResultClickListener() },
                    searchContentMatch.lines
                )
            }
    }
}
