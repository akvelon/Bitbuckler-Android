package com.akvelon.bitbuckler.domain.prlink

import android.content.Context
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.entity.pullrequest.PullRequestParseEntity
import com.akvelon.bitbuckler.extension.isValidUrl
import com.akvelon.bitbuckler.model.entity.args.PullRequestArgs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.RuntimeException
import kotlin.coroutines.resumeWithException

class BitbucketPRLinkParser(
    private val context: Context
): PRLinkParser {

    @ExperimentalCoroutinesApi
    override suspend fun parseLink(url: String): PullRequestParseEntity {
        return suspendCancellableCoroutine { emitter ->
            with(url) {
                when {
                    !url.isValidUrl() || url.isEmpty() -> {
                        emitter.resume(PullRequestParseEntity.PRParseFailed(context.getString(R.string.url_should_be_valid))) {
                            throw RuntimeException(it.message)
                        }
                    }
                    !contains("https://bitbucket.org") -> {
                        emitter.resume(PullRequestParseEntity.PRParseFailed(context.getString(R.string.not_a_bitbucket_repository))) {
                            throw RuntimeException(it.message)
                        }
                    }
                    else -> {
                        val urlPrefix = url.substring(url.indexOf(".org") + 5)
                        val arr = urlPrefix.split("/")
                        try {
                            val args = PullRequestArgs(
                                workspaceSlug = arr[0],
                                repositorySlug = arr[1],
                                id = arr[3].toInt()
                            )
                            emitter.resume(PullRequestParseEntity.PRParseSuccess(args)) {
                                throw RuntimeException(it.message)
                            }
                        } catch (ex: Exception) {
                            emitter.resumeWithException(ex)
                        }
                    }
                }
            }
        }
    }
}