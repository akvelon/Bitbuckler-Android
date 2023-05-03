/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 11 January 2021
 */

package com.akvelon.bitbuckler.ui.screen.comment.adapter

import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemCommentBinding
import com.akvelon.bitbuckler.extension.*
import com.akvelon.bitbuckler.model.entity.TreeNode
import com.akvelon.bitbuckler.model.entity.User
import com.akvelon.bitbuckler.model.entity.comment.Comment
import com.akvelon.bitbuckler.ui.base.BaseViewHolder
import io.noties.markwon.Markwon

class CommentListAdapter(
    private val markwon: Markwon,
    private val replyClickListener: (Comment, String) -> Unit,
    private val editClickListener: (Comment) -> Unit,
    private val deleteClickListener: (Comment) -> Unit,
    private val comments: MutableList<TreeNode<Comment>> = mutableListOf(),
) : RecyclerView.Adapter<CommentListAdapter.CommentHolder>() {

    var user: User? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CommentHolder(parent.inflate(R.layout.item_comment))

    override fun onBindViewHolder(holder: CommentHolder, position: Int) =
        holder.bind(comments[position])

    override fun getItemCount() = comments.count()

    fun setComments(newComments: List<TreeNode<Comment>>) {
        val diffResult = DiffUtil.calculateDiff(
            CommentDiffUtilCallback(comments, newComments)
        )

        comments.apply {
            clear()
            addAll(newComments)
        }

        diffResult.dispatchUpdatesTo(this)
    }

    inner class CommentHolder(itemView: View) : BaseViewHolder(itemView) {
        private val binding by viewBinding(ItemCommentBinding::bind)
        private var commentAuthor: String? = null

        fun bind(node: TreeNode<Comment>) {
            val comment = node.element
            commentAuthor = comment.user.displayName

            with(binding) {
                reply.setOnClickListener {
                    replyClickListener(
                        comments[bindingAdapterPosition].element,
                        commentAuthor!!
                    )
                }

                edit.setOnClickListener {
                    comments[bindingAdapterPosition].element
                    editClickListener(comments[bindingAdapterPosition].element)
                }
                delete.setOnClickListener { deleteClickListener(comments[bindingAdapterPosition].element) }

                levelSpace.layoutParams =
                    (levelSpace.layoutParams as ConstraintLayout.LayoutParams).apply {
                        marginStart = calculateMargin(node)
                    }

                avatar.loadCircle(comment.user.links.avatar.href)
                author.text = commentAuthor
                createdOn.text = comment.createdOn.timeAgoShort(itemView.context)

                if (comment.deleted) {
                    content.apply {
                        text = itemView.getString(R.string.pr_comment_deleted)
                        setItalicTypeface()
                    }

                    accountActionGroup.hide()
                    reply.hide()
                } else {
                    content.setNormalTypeface()

                    val rawContent = comment.content.mentionedRaw ?: comment.content.raw
                    markwon.setMarkdown(content, rawContent)
                }

                if (comment.user.uuid != user?.uuid) {
                    accountActionGroup.hide()
                }
            }
        }

        private fun calculateMargin(node: TreeNode<Comment>) =
            (itemView.resources.getDimension(R.dimen.spacingNormal) * (node.level + 1)).toInt()
    }
}
