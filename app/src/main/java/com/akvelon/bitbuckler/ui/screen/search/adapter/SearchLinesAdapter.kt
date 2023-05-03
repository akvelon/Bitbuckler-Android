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
import com.akvelon.bitbuckler.databinding.ItemSearchLineBinding
import com.akvelon.bitbuckler.extension.inflate
import com.akvelon.bitbuckler.model.entity.search.SearchLine
import com.akvelon.bitbuckler.ui.base.BaseViewHolder

class SearchLinesAdapter(
    private val onResultClickListener: () -> Unit,
    val lines: List<SearchLine>
) : RecyclerView.Adapter<SearchLinesAdapter.SearchLineHolder>() {

    private val maxLengthLineNumber = lines.last().line.toString().length

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SearchLineHolder(parent.inflate(R.layout.item_search_line))

    override fun onBindViewHolder(holder: SearchLineHolder, position: Int) {
        holder.bind(lines[position])
    }

    override fun getItemCount() = lines.size

    inner class SearchLineHolder(itemView: View) : BaseViewHolder(itemView) {
        private val binding by viewBinding(ItemSearchLineBinding::bind)

        init {
            itemView.setOnClickListener {
                onResultClickListener()
            }
        }

        fun bind(searchLine: SearchLine) =
            with(binding) {
                lineNumber.text = searchLine.line
                    .toString().padStart(maxLengthLineNumber, ' ')
                segments.adapter = SegmentsAdapter(
                    { onResultClickListener() },
                    searchLine.segments
                )
            }
    }
}
