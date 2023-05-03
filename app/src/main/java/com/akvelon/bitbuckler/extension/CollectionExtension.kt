/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 14 January 2021
 */

package com.akvelon.bitbuckler.extension

import com.akvelon.bitbuckler.model.entity.Slugs
import com.akvelon.bitbuckler.model.entity.TreeNode
import com.akvelon.bitbuckler.model.entity.comment.Comment
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequest
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequestType
import com.akvelon.bitbuckler.model.entity.pullrequest.TrackedPullRequest
import com.akvelon.bitbuckler.model.entity.source.Source

fun <T> Iterable<T>.dropFirst() = this.drop(1)

fun MutableList<Comment>.createCommentsTreeList(): List<TreeNode<Comment>> {
    val parentList = mutableListOf<TreeNode<Comment>>()

    removeIf { comment ->
        if (comment.isParent) {
            parentList.add(TreeNode(comment.id, comment))
            true
        } else {
            false
        }
    }

    while (isNotEmpty()) {
        removeIf { comment ->
            parentList.forEach { parent ->
                comment.parent?.let {
                    val node = parent.findById(it.id)
                    if (node != null) {
                        node.addChild(TreeNode(comment.id, comment, node.level + 1))
                        return@removeIf true
                    }
                }
            }
            false
        }
    }

    parentList.removeDeletedComments()

    return parentList.toList()
}

fun MutableList<TreeNode<Comment>>.removeDeletedComments() {
    removeIf { parent ->
        parent.removeIf { node ->
            node.element.deleted && node.hasNoChild
        }
        parent.isEmptyTree
    }
}

fun <T> MutableList<T>.replaceElement(oldElement: T, newElement: T) {
    val index = indexOf(oldElement)

    if (index != -1) {
        removeAt(index)
        add(index, newElement)
    }
}

fun List<TrackedPullRequest>.setType(type: PullRequestType) = this.map { it.type = type }

fun List<PullRequest>.toTracked(slugs: Slugs ?= null) =
    this.map { it.toTracked(slugs = slugs ?: Slugs(workspace = it.workspaceSlug, repository = it.repositorySlug)) }

fun List<Source>.isRoot() = isNotEmpty() && first().name == first().path

fun <T> MutableList<T>.addAllWithIntersection(insertion: List<T>) {
    if (isEmpty() ) {
        addAll(insertion)
    } else {
        val endIntersectIdx = insertion.indexOf(last())
        if (endIntersectIdx == -1) {
            // No intersection
            addAll(insertion)
        } else {
            addAll(insertion.subList(endIntersectIdx+1, insertion.size))
        }
    }
}
