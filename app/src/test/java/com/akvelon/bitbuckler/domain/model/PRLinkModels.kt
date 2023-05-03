package com.akvelon.bitbuckler.domain.model

import com.akvelon.bitbuckler.model.entity.args.PullRequestArgs

object PRLinkModels {
    const val EMPTY_LINK = ""
    const val MALFORMED_LINK = "httpst://bad.link.org"
    const val NAN_PR_LINK = "https://www.evoca.am/hy/general-information-history-key-values/"

    const val SAMPLE_PR_LINK = "https://bitbucket.org/movietripv2/movietripv2android/pull-requests/1"
    val sampleRequestParams = PullRequestArgs(
        workspaceSlug = "movietripv2",
        repositorySlug = "movietripv2android",
        id = 1
    )
}