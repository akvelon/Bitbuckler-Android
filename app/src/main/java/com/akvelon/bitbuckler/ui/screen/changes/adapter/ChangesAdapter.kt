/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 12 January 2021
 */

package com.akvelon.bitbuckler.ui.screen.changes.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemChangesBodyBinding
import com.akvelon.bitbuckler.databinding.ItemChangesHeaderBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.file.FileDiff
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.file.FileStat
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.LineDiff
import com.akvelon.bitbuckler.ui.base.BaseViewHolder
import com.akvelon.bitbuckler.ui.state.screen.DiffDetails

class ChangesAdapter(
    private val diffDetails: DiffDetails,
    private val onCommentClickListener: (FileDiff, LineDiff?, Int, Int?) -> Unit,
) : RecyclerView.Adapter<BaseViewHolder>() {

    companion object {
        const val HEADER_VIEW_TYPE = 0
        const val BODY_VIEW_TYPE = 1
        const val LARGE_DIFF_VIEW_TYPE = 2
    }

    override fun getItemViewType(position: Int) =
        when {
            position % 2 == 0 -> HEADER_VIEW_TYPE
            diffDetails.changes[position / 2].isLarge -> LARGE_DIFF_VIEW_TYPE
            else -> BODY_VIEW_TYPE
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            HEADER_VIEW_TYPE ->
                HeaderHolder(parent.inflate(R.layout.item_changes_header))
            LARGE_DIFF_VIEW_TYPE ->
                LargeDiffHolder(parent.inflate(R.layout.item_diff_too_large))
            else ->
                BodyHolder(parent.inflate(R.layout.item_changes_body))
        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = with(diffDetails) {
        if (holder is HeaderHolder) {
            holder.bind(changes[position / 2], files[position / 2])
        } else if (holder is BodyHolder) {
            holder.bind(changes[position / 2], position)
        }
    }

    override fun getItemCount() = diffDetails.changes.size * 2

    inner class HeaderHolder(itemView: View) : BaseViewHolder(itemView) {
        private val binding by viewBinding(ItemChangesHeaderBinding::bind)

        init {
            binding.root.setOnClickListener {
                onCommentClickListener(
                    diffDetails.changes[bindingAdapterPosition / 2],
                    null,
                    bindingAdapterPosition / 2,
                    null
                )
            }

            binding.inlineFileComment.root.setOnClickListener {
                onCommentClickListener(
                    diffDetails.changes[bindingAdapterPosition / 2],
                    null,
                    bindingAdapterPosition / 2,
                    null
                )
            }
        }

        fun bind(file: FileDiff, fileStat: FileStat) =
            with(binding) {
                fileStatus.setImageResource(file.status.getStatusIcon())
                filePath.text = file.getFormattingPath()
                isMergeConflict.setVisible(file.isMergeConflict)

                if (file.comments.isNotEmpty()) {
                    bindComments(file)
                } else {
                    binding.inlineFileComment.root.hide()
                }

                setFileChanges(tvAddedLines, R.string.lines_added, fileStat.linesAdded)
                setFileChanges(tvRemovedLines, R.string.lines_removed, fileStat.linesRemoved)
            }

        private fun setFileChanges(textView: TextView, stringResId: Int, value: Int) {
            if (value > 0) {
                textView.setVisible(true)
                textView.text = itemView.getString(stringResId, value)
            }
        }

        private fun bindComments(file: FileDiff) {
            with(binding.inlineFileComment) {
                with(file.comments.last().element) {
                    avatar.loadCircle(user.links.avatar.href)
                    author.text = user.displayName

                    if (deleted) {
                        comment.text =
                            itemView.getString(R.string.pr_comment_deleted)
                        comment.setItalicTypeface()
                    } else {
                        comment.text = content.getRenderedContent()
                    }

                    commentCount.text =
                        file.comments.sumOf { node -> node.count }.toString()
                    root.show()
                }
            }
        }
    }

    inner class BodyHolder(itemView: View) : BaseViewHolder(itemView) {
        private val binding by viewBinding(ItemChangesBodyBinding::bind)

        fun bind(file: FileDiff, position: Int) =
            binding.recyclerViewDiffs.apply {
                setHasFixedSize(true)
                adapter = LinesAdapter(
                    file,
                    position,
                    onCommentClickListener
                )
            }
    }

    inner class LargeDiffHolder(itemView: View) : BaseViewHolder(itemView)
}
