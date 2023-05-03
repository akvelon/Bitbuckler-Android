/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 30 April 2021
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.source.list.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemSourceBranchBinding
import com.akvelon.bitbuckler.extension.inflate
import com.akvelon.bitbuckler.model.entity.repository.Branch
import com.akvelon.bitbuckler.ui.base.BaseViewHolder

class SelectBranchAdapter(
    private val branches: List<Branch>,
    private val onBranchClickListener: (Branch) -> Unit
) : RecyclerView.Adapter<BaseViewHolder>() {

    companion object {
        const val ITEM_VIEW_TYPE = 0
        const val EMPTY_VIEW_TYPE = 1
    }

    override fun getItemViewType(position: Int) =
        if (branches.isEmpty()) EMPTY_VIEW_TYPE else ITEM_VIEW_TYPE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == ITEM_VIEW_TYPE) {
            BranchHolder(parent.inflate(R.layout.item_source_branch))
        } else {
            EmptyHolder(parent.inflate(R.layout.layout_empty_tags))
        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is BranchHolder) {
            holder.bind(branches[position])
        }
    }

    override fun getItemCount() = if (branches.isEmpty()) 1 else branches.size

    inner class BranchHolder(itemView: View) : BaseViewHolder(itemView) {
        private val binding by viewBinding(ItemSourceBranchBinding::bind)

        init {
            itemView.setOnClickListener {
                onBranchClickListener(branches[bindingAdapterPosition])
            }
        }

        fun bind(branch: Branch) {
            binding.name.text = branch.name
        }
    }

    inner class EmptyHolder(itemView: View) : BaseViewHolder(itemView)
}
