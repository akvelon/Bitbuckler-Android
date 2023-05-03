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
import com.akvelon.bitbuckler.databinding.ItemSearchSegmentBinding
import com.akvelon.bitbuckler.extension.getColor
import com.akvelon.bitbuckler.extension.inflate
import com.akvelon.bitbuckler.model.entity.search.SearchSegment
import com.akvelon.bitbuckler.ui.base.BaseViewHolder

class SegmentsAdapter(
    private val onResultClickListener: () -> Unit,
    private val segments: List<SearchSegment>
) : RecyclerView.Adapter<SegmentsAdapter.SegmentHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SegmentHolder(parent.inflate(R.layout.item_search_segment))

    override fun onBindViewHolder(holder: SegmentHolder, position: Int) =
        holder.bind(segments[position])

    override fun getItemCount() = segments.size

    inner class SegmentHolder(itemView: View) : BaseViewHolder(itemView) {
        private val binding by viewBinding(ItemSearchSegmentBinding::bind)

        init {
            itemView.setOnClickListener {
                onResultClickListener()
            }
        }

        fun bind(searchSegment: SearchSegment) =
            with(binding) {
                if (searchSegment.match) {
                    code.setBackgroundColor(
                        root.getColor(R.color.orangeHighlight)
                    )
                }

                code.text = searchSegment.text
            }
    }
}
