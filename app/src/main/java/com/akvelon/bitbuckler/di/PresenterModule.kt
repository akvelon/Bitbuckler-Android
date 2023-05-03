/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 22 December 2020
 */

package com.akvelon.bitbuckler.di

import android.content.Context
import com.akvelon.bitbuckler.domain.prlink.BitbucketPRLinkParser
import com.akvelon.bitbuckler.domain.prlink.PRLinkParser
import com.akvelon.bitbuckler.model.entity.Project
import com.akvelon.bitbuckler.model.entity.Slugs
import com.akvelon.bitbuckler.model.entity.Workspace
import com.akvelon.bitbuckler.model.entity.args.*
import com.akvelon.bitbuckler.ui.screen.AppPresenter
import com.akvelon.bitbuckler.ui.screen.account.AccountPresenter
import com.akvelon.bitbuckler.ui.screen.activity.ActivityListPresenter
import com.akvelon.bitbuckler.ui.screen.activity.dialog.PrLinkPresenter
import com.akvelon.bitbuckler.ui.screen.changes.ChangesPresenter
import com.akvelon.bitbuckler.ui.screen.comment.list.CommentListPresenter
import com.akvelon.bitbuckler.ui.screen.commit.CommitDetailsPresenter
import com.akvelon.bitbuckler.ui.screen.main.MainPresenter
import com.akvelon.bitbuckler.ui.screen.privacypolicy.PrivacyPolicyPresenter
import com.akvelon.bitbuckler.ui.screen.project.list.ProjectListPresenter
import com.akvelon.bitbuckler.ui.screen.pullrequest.PullRequestDetailsPresenter
import com.akvelon.bitbuckler.ui.screen.pullrequest.comment.inline.InlineCommentsPresenter
import com.akvelon.bitbuckler.ui.screen.pullrequest.commits.PrCommitListPresenter
import com.akvelon.bitbuckler.ui.screen.repository.TrackingInfoPresenter
import com.akvelon.bitbuckler.ui.screen.repository.details.RepositoryDetailsPresenter
import com.akvelon.bitbuckler.ui.screen.repository.details.branches.BranchListPresenter
import com.akvelon.bitbuckler.ui.screen.repository.details.commits.CommitListPresenter
import com.akvelon.bitbuckler.ui.screen.repository.details.issues.details.IssueDetailsPresenter
import com.akvelon.bitbuckler.ui.screen.repository.details.issues.list.IssueListPresenter
import com.akvelon.bitbuckler.ui.screen.repository.details.pullrequests.PullRequestListPresenter
import com.akvelon.bitbuckler.ui.screen.repository.details.source.details.SourceDetailsPresenter
import com.akvelon.bitbuckler.ui.screen.repository.details.source.list.SourceListPresenter
import com.akvelon.bitbuckler.ui.screen.repository.list.RepositoryListPresenter
import com.akvelon.bitbuckler.ui.screen.search.SearchPresenter
import com.akvelon.bitbuckler.ui.screen.start.StartPresenter
import com.akvelon.bitbuckler.ui.screen.track_repo.TrackRepoSelectionPresenter
import com.akvelon.bitbuckler.ui.screen.workspace.WorkspaceListPresenter
import com.github.terrakok.cicerone.Router
import org.koin.dsl.module

fun presenterModule(context: Context) = module {

    single<PRLinkParser> {
        BitbucketPRLinkParser(context)
    }

    factory { (router: Router) ->
        AppPresenter(router, get(), get(), get(), get(), get(), get())
    }

    factory { (router: Router) ->
        StartPresenter(router, get())
    }

    factory {
        MainPresenter(get(), get(), get())
    }

    factory { (router: Router) ->
        ActivityListPresenter(router, get(), get(), get(), get(), get(), get(), get(), get(), get(), get())
    }

    factory { (router: Router) ->
        PrLinkPresenter(router, get(), get())
    }

    factory { (router: Router) ->
        WorkspaceListPresenter(router, get(), get(), get(), get())
    }

    factory { (router: Router) ->
        AccountPresenter(router, get(), get(), get(), get(), get(), get(), get())
    }

    factory { (router: Router, workspace: Workspace) ->
        ProjectListPresenter(router, workspace, get())
    }

    factory { (router: Router, workspace: Workspace, project: Project) ->
        RepositoryListPresenter(router, workspace, project, get(), get())
    }

    factory { (router: Router, slugs: Slugs) ->
        RepositoryDetailsPresenter(router, slugs, get(), get(), get(), get())
    }

    factory { (router: Router, sourceArgs: SourceArgs) ->
        SourceListPresenter(router, sourceArgs, get(), get(), get(), get())
    }

    factory { (router: Router, sourceDetailsArgs: SourceDetailsArgs) ->
        SourceDetailsPresenter(router, sourceDetailsArgs, get())
    }

    factory { (router: Router, commitsArgs: CommitsArgs) ->
        CommitListPresenter(router, commitsArgs, get(), get(), get())
    }

    factory { (router: Router, slugs: Slugs) ->
        PullRequestListPresenter(router, slugs, get(), get(), get())
    }

    factory { (router: Router, slugs: Slugs) ->
        BranchListPresenter(router, slugs, get(), get())
    }

    factory { (router: Router, slugs: Slugs) ->
        IssueListPresenter(router, slugs, get(), get(), get())
    }

    factory { (router: Router, issueArgs: IssueArgs) ->
        IssueDetailsPresenter(router, issueArgs, get(), get(), get(), get())
    }

    factory { (router: Router, pullRequestArgs: PullRequestArgs) ->
        PullRequestDetailsPresenter(router, pullRequestArgs, get(), get(), get(), get(), get(), get())
    }

    factory { (router: Router, changesArgs: ChangesArgs) ->
        ChangesPresenter(router, changesArgs, get(), get(), get(), get())
    }

    factory { (router: Router, commentsArgs: CommentsArgs) ->
        CommentListPresenter(router, commentsArgs, get(), get(), get(), get())
    }

    factory { (router: Router, inlineCommentsArgs: InlineCommentsArgs) ->
        InlineCommentsPresenter(router, inlineCommentsArgs, get(), get(), get())
    }

    factory { (router: Router, commitDetailsArgs: CommitDetailsArgs) ->
        CommitDetailsPresenter(router, commitDetailsArgs, get(), get())
    }

    factory { (router: Router, pullRequestArgs: PullRequestArgs) ->
        PrCommitListPresenter(router, pullRequestArgs, get(), get())
    }

    factory { (router: Router, workspace: Workspace) ->
        SearchPresenter(router, workspace, get())
    }

    factory { (router: Router) ->
        TrackRepoSelectionPresenter(router, get(), get(), get(), get())
    }

    single { (router: Router) ->
        TrackingInfoPresenter(router, get(), get())
    }

    factory { (router: Router) ->
        PrivacyPolicyPresenter(router, context, get(), get(), get(), get())
    }
}
