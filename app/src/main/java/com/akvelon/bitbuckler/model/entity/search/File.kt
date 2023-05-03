/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Alexander Vinogradov (a.vinogradov@akvelon.com)
 * on 09 December 2021
 */

package com.akvelon.bitbuckler.model.entity.search

class File(

    val path: String,

    val type: String,

    val links: Links,
) {

    fun getWorkspace() = getUriSegments()[WORKSPACE_SEGMENT_NUMBER]

    fun getRepo() = getUriSegments()[REPO_SEGMENT_NUMBER]

    fun getCommitHash() = getUriSegments()[COMMIT_SEGMENT_NUMBER]

    private fun getUriSegments(): List<String> {
        var uri = links.self.href
        uri = uri.substring(uri.indexOf(REPOSITORIES))
        return uri.split("/")
    }

    companion object {
        const val REPOSITORIES = "repositories"
        const val WORKSPACE_SEGMENT_NUMBER = 1
        const val REPO_SEGMENT_NUMBER = 2
        const val COMMIT_SEGMENT_NUMBER = 4
    }
}
