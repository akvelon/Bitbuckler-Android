package com.akvelon.bitbuckler.ui.viewholders

import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemRepositoryBinding
import com.akvelon.bitbuckler.extension.getString
import com.akvelon.bitbuckler.extension.loadSquare
import com.akvelon.bitbuckler.extension.show
import com.akvelon.bitbuckler.extension.timeAgoForRepos
import com.akvelon.bitbuckler.model.entity.repository.Repository
import com.akvelon.bitbuckler.ui.base.BaseViewHolder

class RepositoryHolder(
    itemView: View,
    clickListener: (Repository) -> Unit,
    private val setTrackListener: ((Repository, Boolean) -> Unit)?
) : BaseViewHolder(itemView) {
    private val binding by viewBinding(ItemRepositoryBinding::bind)
    private lateinit var item: Repository
    init {
        itemView.setOnClickListener {
            clickListener(item)
        }
    }

    fun bind(repository: Repository) =
        with(binding) {
            item = repository
            repository.let {
                name.text = it.name
                projectName.text = it.project.name
                avatar.loadSquare(it.links.avatar.href, R.drawable.ic_repository)
                tvUpdatedOn.text = itemView.getString(
                    R.string.updated,
                    it.updatedOn.timeAgoForRepos(itemView.context)
                )
                trackedButton.isChecked = repository.isTracked
                if (setTrackListener == null) {
                    trackedButtonContainer.isClickable = false
                } else {
                    trackedButtonContainer.setOnClickListener {
                        setTrackListener.invoke(repository, !repository.isTracked)
                        trackedButton.isChecked = trackedButton.isChecked.not()
                    }
                }

                if (it.isPrivate) privacy.show()
            }
        }
}