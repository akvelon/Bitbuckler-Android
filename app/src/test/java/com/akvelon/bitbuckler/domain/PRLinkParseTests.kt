package com.akvelon.bitbuckler.domain

import android.content.Context
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.common.UserErrors
import com.akvelon.bitbuckler.domain.model.PRLinkModels
import com.akvelon.bitbuckler.domain.prlink.BitbucketPRLinkParser
import com.akvelon.bitbuckler.domain.prlink.PRLinkParser
import com.akvelon.bitbuckler.entity.pullrequest.PullRequestParseEntity
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PRLinkParseTests {

    private lateinit var parser: PRLinkParser

    @BeforeEach
    fun setup() {
        val context: Context = mock(Context::class.java)
        Mockito.`when`(context.getString(R.string.url_should_be_valid)).thenReturn("Url should be valid")
        Mockito.`when`(context.getString(R.string.not_a_bitbucket_repository)).thenReturn("Not a bitbucket repository")
        parser = BitbucketPRLinkParser(context)
    }

    @Test
    fun `link parse success test`() = runBlocking {
        parser.parseLink(PRLinkModels.SAMPLE_PR_LINK).let { value ->
            assert(
                value is PullRequestParseEntity.PRParseSuccess
                        && value.args.workspaceSlug == PRLinkModels.sampleRequestParams.workspaceSlug
                        && value.args.repositorySlug == PRLinkModels.sampleRequestParams.repositorySlug
                        && value.args.id == PRLinkModels.sampleRequestParams.id
            )
        }
    }

    @Test
    fun `no bitbucket pr link test`() = runBlocking {
        parser.parseLink(PRLinkModels.NAN_PR_LINK).let { value ->
            assert(
                value is PullRequestParseEntity.PRParseFailed
                        && value.message == UserErrors.NOT_A_BITBUCKET_REPOSITORY
            )
        }
    }

    @Test
    fun `not valid url test`() = runBlocking {
        parser.parseLink(PRLinkModels.MALFORMED_LINK).let { value ->
            assert(
                value is PullRequestParseEntity.PRParseFailed
                        && value.message == UserErrors.URL_SHOULD_BE_VALID
            )
        }
    }

    @Test
    fun `empty url test`() = runBlocking {
        parser.parseLink(PRLinkModels.EMPTY_LINK).let { value ->
            assert(
                value is PullRequestParseEntity.PRParseFailed
                        && value.message == UserErrors.URL_SHOULD_BE_VALID
            )
        }
    }
}