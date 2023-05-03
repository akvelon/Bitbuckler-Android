package com.akvelon.bitbuckler.ui.screen.pullrequest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.akvelon.bitbuckler.databinding.ItemApprovedByBinding

class ApprovedByAdapter :
    ListAdapter<String, ApprovedByAdapter.ApprovedByViewHolder>(ApprovedByDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApprovedByViewHolder {
        val binding = ItemApprovedByBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ApprovedByViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ApprovedByViewHolder, position: Int) {
        val item = getItem(position)
        holder.onBind(item)
    }

    inner class ApprovedByViewHolder(private val binding: ItemApprovedByBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: String) = with(binding) {
            tvName.text = item
        }
    }

    private class ApprovedByDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}
