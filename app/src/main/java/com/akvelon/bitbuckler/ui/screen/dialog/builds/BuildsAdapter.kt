/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 19 February 2021
 */

package com.akvelon.bitbuckler.ui.screen.dialog.builds

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemBuildBinding
import com.akvelon.bitbuckler.extension.getStateIcon
import com.akvelon.bitbuckler.extension.inflate
import com.akvelon.bitbuckler.extension.setTextOrHide
import com.akvelon.bitbuckler.extension.timeAgo
import com.akvelon.bitbuckler.model.entity.statuses.Status
import com.akvelon.bitbuckler.ui.base.BaseViewHolder

class BuildsAdapter(
    private val builds: List<Status>
) : RecyclerView.Adapter<BuildsAdapter.BuildHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BuildHolder(parent.inflate(R.layout.item_build))

    override fun onBindViewHolder(holder: BuildHolder, position: Int) =
        holder.bind(builds[position])

    override fun getItemCount() = builds.size

    inner class BuildHolder(itemView: View) : BaseViewHolder(itemView) {
        private val binding by viewBinding(ItemBuildBinding::bind)

        fun bind(build: Status) =
            with(binding) {
                title.text = build.name
                description.setTextOrHide(build.description)
                updatedOn.text = build.updatedOn.timeAgo(itemView.context)
                state.setImageResource(build.getStateIcon())
            }
    }
}
