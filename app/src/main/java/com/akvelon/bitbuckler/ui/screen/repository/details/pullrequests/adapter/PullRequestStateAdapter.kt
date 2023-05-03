/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 15 February 2021
 */

package com.akvelon.bitbuckler.ui.screen.repository.details.pullrequests.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemPrStateBinding
import com.akvelon.bitbuckler.databinding.ItemPrStateDropdownBinding
import com.akvelon.bitbuckler.extension.getStringRes
import com.akvelon.bitbuckler.extension.inflate
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequestState

class PullRequestStateAdapter(
    private val items: Array<PullRequestState>
) : BaseAdapter() {

    override fun getCount() = items.size

    override fun getItem(position: Int) = items[position]

    override fun getItemId(position: Int) = -1L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding =
            ItemPrStateBinding.bind(
                convertView ?: parent.inflate(R.layout.item_pr_state)
            )
        binding.state.setText(items[position].getStringRes())
        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding =
            ItemPrStateDropdownBinding.bind(
                convertView ?: parent.inflate(R.layout.item_pr_state_dropdown)
            )
        binding.state.setText(items[position].getStringRes())
        return binding.root
    }
}
