/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 01 February 2021
 */

package com.akvelon.bitbuckler.ui.screen.changes.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemLineDiffBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.file.FileDiff
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.BinaryLine
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.CollapsedLine
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.LineDiff
import com.akvelon.bitbuckler.ui.base.BaseViewHolder

class LinesAdapter(
    private val file: FileDiff,
    private val filePosition: Int,
    private val onLineClickListener: (FileDiff, LineDiff, Int, Int) -> Unit,
) : RecyclerView.Adapter<LinesAdapter.LineHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LineHolder(parent.inflate(R.layout.item_line_diff))

    override fun onBindViewHolder(holder: LineHolder, position: Int) =
        holder.bind(file.lines[position])

    override fun getItemCount() = file.lines.size

    inner class LineHolder(itemView: View) : BaseViewHolder(itemView) {
        private val binding by viewBinding(ItemLineDiffBinding::bind)

        init {
            binding.root.setOnClickListener {
                when (file.lines[bindingAdapterPosition]) {
                    is CollapsedLine, is BinaryLine -> {}
                    else -> onLineClickListener(
                        file,
                        file.lines[bindingAdapterPosition],
                        filePosition,
                        bindingAdapterPosition
                    )
                }
            }

            binding.inlineComment.root.setOnClickListener {
                onLineClickListener(
                    file,
                    file.lines[bindingAdapterPosition],
                    filePosition,
                    bindingAdapterPosition
                )
            }
        }

        fun bind(line: LineDiff) =
            with(binding) {
                lineView.setBackgroundResource(line.getLineBackground())

                with(getMaxLengthLineNumber()) {
                    oldLineNumber.text = line.oldNumberToString(this)
                    newLineNumber.text = line.newNumberToString(this)
                }

                prefix.setTextOrHide(line.getPrefix())

                if (line is BinaryLine) {
                    tvContent.text = tvContent.getString(R.string.line_binary_content)
                } else {
                    tvContent.text = line.content
                }

                if (line.comments.isNotEmpty()) bindComments(line)
            }

        private fun bindComments(line: LineDiff) {
            with(binding.inlineComment) {
                with(line.comments.first().element) {
                    avatar.loadCircle(user.links.avatar.href)
                    author.text = user.displayName

                    if (deleted) {
                        comment.text =
                            itemView.getString(R.string.pr_comment_deleted)
                        comment.setItalicTypeface()
                    } else {
                        comment.text = content.getRenderedContent()
                        tvUpdatedOn.text = updatedOn?.timeAgo(root.context)
                    }
                    commentCount.text =
                        line.comments.sumOf { node -> node.count }.toString()
                    root.show()
                }
            }
        }
    }

    private fun getMaxLengthLineNumber() = file.lines.last().getMaxLineNumber().toString().length
}
