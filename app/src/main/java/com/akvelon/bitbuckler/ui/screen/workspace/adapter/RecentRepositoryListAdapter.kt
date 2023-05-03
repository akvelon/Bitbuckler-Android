/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 18 February 2021
 */

package com.akvelon.bitbuckler.ui.screen.workspace.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemRepositoryCardBinding
import com.akvelon.bitbuckler.extension.inflate
import com.akvelon.bitbuckler.extension.loadSquare
import com.akvelon.bitbuckler.extension.show
import com.akvelon.bitbuckler.model.entity.repository.RecentRepository
import com.akvelon.bitbuckler.ui.base.BaseViewHolder

class RecentRepositoryListAdapter(
    private val clickListener: (RecentRepository) -> Unit,
    private val onRemoveListener: (RecentRepository) -> Unit
) : RecyclerView.Adapter<RecentRepositoryListAdapter.RecentRepositoryHolder>() {

    private val items = mutableListOf<RecentRepository>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecentRepositoryHolder(parent.inflate(R.layout.item_repository_card))

    override fun onBindViewHolder(holder: RecentRepositoryHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun setData(recentRepositories: List<RecentRepository>) {
        val diffResult = DiffUtil.calculateDiff(
            RecentRepositoryDiffUtilCallback(items, recentRepositories)
        )

        items.clear()
        items.addAll(recentRepositories)

        diffResult.dispatchUpdatesTo(this)
    }

    inner class RecentRepositoryHolder(itemView: View) : BaseViewHolder(itemView) {
        private val binding by viewBinding(ItemRepositoryCardBinding::bind)

        init {
            itemView.setOnClickListener {
                clickListener(items[bindingAdapterPosition])
            }
            binding.removeRepo.setOnClickListener {
                onRemoveListener(items[bindingAdapterPosition])
            }
        }

        fun bind(recentRepo: RecentRepository) =
            with(binding) {
                name.text = recentRepo.name
                workspaceSlug.text = recentRepo.workspaceSlug

                if (recentRepo.isPrivate) privacy.show()

                avatar.loadSquare(recentRepo.links.avatar.href, R.drawable.ic_repository)
            }
    }
}
