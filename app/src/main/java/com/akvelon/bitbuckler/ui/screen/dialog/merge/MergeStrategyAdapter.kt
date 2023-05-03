/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 12 February 2021
 */

package com.akvelon.bitbuckler.ui.screen.dialog.merge

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemMergeStrategyBinding
import com.akvelon.bitbuckler.databinding.ItemMergeStrategyCollapseBinding
import com.akvelon.bitbuckler.extension.inflate
import com.akvelon.bitbuckler.model.entity.pullrequest.action.MergeStrategyItem

class MergeStrategyAdapter(
    private val items: List<MergeStrategyItem>,
    private val clickListener: SpinnerItemClickListener,
) : BaseAdapter() {

    override fun getCount() = items.size

    override fun getItem(position: Int) = items[position]

    override fun getItemId(position: Int) = -1L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding =
            ItemMergeStrategyCollapseBinding.bind(
                convertView ?: parent.inflate(R.layout.item_merge_strategy_collapse)
            )
        binding.strategyTitle.text = items[position].strategy.title
        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding =
            ItemMergeStrategyBinding.bind(
                convertView ?: parent.inflate(R.layout.item_merge_strategy)
            )
        with(binding) {
            root.setOnClickListener {
                clickListener.onClick(items[position].strategy.title)
            }
            strategyTitle.text = items[position].strategy.title
            strategySubtitle.text = items[position].subtitle
            return root
        }
    }
}
