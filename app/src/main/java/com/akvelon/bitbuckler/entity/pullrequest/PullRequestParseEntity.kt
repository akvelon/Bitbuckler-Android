package com.akvelon.bitbuckler.entity.pullrequest

import com.akvelon.bitbuckler.model.entity.args.PullRequestArgs

sealed class PullRequestParseEntity {
    class PRParseSuccess(val args: PullRequestArgs): PullRequestParseEntity()

    class PRParseFailed(val message: String): PullRequestParseEntity()
}