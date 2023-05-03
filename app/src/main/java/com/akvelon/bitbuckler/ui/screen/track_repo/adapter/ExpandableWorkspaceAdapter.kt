package com.akvelon.bitbuckler.ui.screen.track_repo.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ExpandableTitleWorkspaceBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.repository.Repository
import com.akvelon.bitbuckler.ui.base.BaseViewHolder
import com.akvelon.bitbuckler.ui.pagination.ProgressItem
import com.akvelon.bitbuckler.ui.viewholders.ProgressItemHolder
import com.akvelon.bitbuckler.ui.viewholders.RepositoryHolder
import com.akvelon.bitbuckler.ui.screen.track_repo.WorkspaceExpandableTitle
import com.akvelon.bitbuckler.ui.screen.track_repo.WorkspaceExtended

class ExpandableWorkspaceAdapter(
    private val setTrackListener: (Repository, Boolean) -> Unit,
    private val expandWorkspaceListener: (WorkspaceExtended, Boolean) -> Unit,
) : RecyclerView.Adapter<BaseViewHolder>() {
    private val elements: MutableList<Any> = mutableListOf()

    companion object {
        const val TITLE = 0
        const val CONTENT = 1
        const val PROGRESS = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TITLE -> WorkspaceTitleHolder(parent.inflate(R.layout.expandable_title_workspace)) {
            if (it.isExpanded) collapseWorkspace(it) else expandWorkspace(it)
        }
        PROGRESS -> ProgressItemHolder(parent.inflate(R.layout.item_progress))
        else -> RepositoryHolder(
            parent.inflate(R.layout.item_repository), { repo ->
                run {
                    setTrackListener(repo, !repo.isTracked)
                }
            },
            null
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val data = elements[position]
        if (data is WorkspaceExpandableTitle) {
            holder as WorkspaceTitleHolder
            holder.bind(data)
        } else if (data is Repository) {
            holder as RepositoryHolder
            holder.bind(data)
        }
    }

    override fun getItemCount() = elements.size

    override fun getItemViewType(position: Int) =
        when (elements[position]) {
            is WorkspaceExpandableTitle -> TITLE
            is ProgressItem -> PROGRESS
            else -> CONTENT
        }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(workspaces: List<WorkspaceExtended>) {
        elements.clear()
        elements.addAll(workspaces.map { WorkspaceExpandableTitle(workspace = it) })
        notifyDataSetChanged()
    }

    private fun expandWorkspace(expandable: WorkspaceExpandableTitle){
        expandable.isExpanded = true
        val position = elements.indexOf(expandable)
        val repos = expandable.workspace.listOfRepos
        if (repos == null) {
            elements.add(position + 1, ProgressItem())
            expandable.expandedTilesNumber = 1
            notifyItemInserted(position + 1)
        } else {
            var nextPosition = position
            repos.forEach { repo ->
                elements.add(++nextPosition, repo)
            }
            expandable.expandedTilesNumber = repos.size
            notifyItemRangeInserted(position + 1,  repos.size)
        }
        notifyItemChanged(position)
        expandWorkspaceListener.invoke(expandable.workspace, true)
    }

    private fun collapseWorkspace(expandable: WorkspaceExpandableTitle){
        expandable.isExpanded = false
        val position = elements.indexOf(expandable)
        (1..expandable.expandedTilesNumber).forEach { _ ->
            elements.removeAt(position + 1)
        }
        notifyItemRangeRemoved(position + 1, expandable.expandedTilesNumber)
        notifyItemChanged(position)
        expandWorkspaceListener.invoke(expandable.workspace, false)
    }

    fun updateWorkspace(newWorkspace: WorkspaceExtended) {
        val idx = elements.indexOfFirst { it is WorkspaceExpandableTitle && it.workspace.slug == newWorkspace.slug }
        if (idx == -1) {
            return
        }
        val wsElement = elements[idx] as WorkspaceExpandableTitle
        //Change expandable part
        if (wsElement.isExpanded) {
            //Delete old elements
            collapseWorkspace(wsElement)
            //Change elements
            wsElement.workspace = newWorkspace
            //Add new elements
            expandWorkspace(wsElement)
        } else {
            // No need to update UI
            wsElement.workspace = newWorkspace
        }
        notifyItemChanged(idx)
    }

    fun updateRepository(newWorkspace: WorkspaceExtended, repo: Repository) {
        val idx = elements.indexOfFirst { it is WorkspaceExpandableTitle && it.workspace.slug == newWorkspace.slug }
        val wsElement = elements[idx] as WorkspaceExpandableTitle
        wsElement.workspace = newWorkspace
        notifyItemChanged(idx)
        if (wsElement.isExpanded) {
            // Repository is on the screen. Update it.
            val repoIdx = elements.indexOf(repo)
            if (repoIdx != -1) {
                notifyItemChanged(repoIdx)
            }
        }
    }
}

class WorkspaceTitleHolder(
    itemView: View,
    clickListener: (WorkspaceExpandableTitle) -> Unit,
) : BaseViewHolder(itemView) {
    private val binding by viewBinding(ExpandableTitleWorkspaceBinding::bind)
    private lateinit var item: WorkspaceExpandableTitle
    init {
        itemView.setOnClickListener {
            clickListener(item)
        }
    }

    fun bind(titleWorkspace: WorkspaceExpandableTitle) {
        item = titleWorkspace

        with(binding) {
            titleWorkspace.let {
                val trackedNumber = titleWorkspace.workspace.trackedNumber
                trackNumber.text = when {
                    trackedNumber == null -> itemView.getString(R.string.loading)
                    trackedNumber > 0 -> itemView.getString(
                        R.string.track_number_selected_repos,
                        trackedNumber
                    )
                    else -> itemView.getString(R.string.track_no_selected_repos)
                }
                if (titleWorkspace.isExpanded) {
                    icExpand.setImageResource(R.drawable.ic_dropdown_up)
                } else {
                    icExpand.setImageResource(R.drawable.ic_dropdown)
                }
                name.text = titleWorkspace.workspace.name
            }
        }
    }
}