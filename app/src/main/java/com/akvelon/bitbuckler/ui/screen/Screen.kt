/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 20 November 2020
 */

package com.akvelon.bitbuckler.ui.screen

import com.akvelon.bitbuckler.model.entity.Project
import com.akvelon.bitbuckler.model.entity.Slugs
import com.akvelon.bitbuckler.model.entity.Workspace
import com.akvelon.bitbuckler.model.entity.args.*
import com.akvelon.bitbuckler.ui.navigation.TabTags
import com.akvelon.bitbuckler.ui.screen.account.AccountFragment
import com.akvelon.bitbuckler.ui.screen.account.AccountTabFragment
import com.akvelon.bitbuckler.ui.screen.activity.ActivityListFragment
import com.akvelon.bitbuckler.ui.screen.activity.ActivityTabFragment
import com.akvelon.bitbuckler.ui.screen.changes.ChangesFragment
import com.akvelon.bitbuckler.ui.screen.commit.CommitDetailsFragment
import com.akvelon.bitbuckler.ui.screen.main.MainFragment
import com.akvelon.bitbuckler.ui.screen.privacypolicy.LOAD_TYPE_DEFAULT
import com.akvelon.bitbuckler.ui.screen.privacypolicy.PrivacyPolicyFragment
import com.akvelon.bitbuckler.ui.screen.project.list.ProjectListFragment
import com.akvelon.bitbuckler.ui.screen.pullrequest.PullRequestDetailsFragment
import com.akvelon.bitbuckler.ui.screen.pullrequest.comment.inline.InlineCommentsFragment
import com.akvelon.bitbuckler.ui.screen.pullrequest.commits.PrCommitListFragment
import com.akvelon.bitbuckler.ui.screen.repository.details.RepositoryDetailsFragment
import com.akvelon.bitbuckler.ui.screen.repository.details.branches.BranchListFragment
import com.akvelon.bitbuckler.ui.screen.repository.details.commits.CommitListFragment
import com.akvelon.bitbuckler.ui.screen.repository.details.issues.details.IssueDetailsFragment
import com.akvelon.bitbuckler.ui.screen.repository.details.issues.list.IssueListFragment
import com.akvelon.bitbuckler.ui.screen.repository.details.pullrequests.PullRequestListFragment
import com.akvelon.bitbuckler.ui.screen.repository.details.source.details.SourceDetailsFragment
import com.akvelon.bitbuckler.ui.screen.repository.details.source.list.SourceListFragment
import com.akvelon.bitbuckler.ui.screen.repository.list.RepositoryListFragment
import com.akvelon.bitbuckler.ui.screen.search.SearchFragment
import com.akvelon.bitbuckler.ui.screen.start.StartFragment
import com.akvelon.bitbuckler.ui.screen.track_repo.TrackRepoSelectionFragment
import com.akvelon.bitbuckler.ui.screen.workspace.WorkspaceListFragment
import com.akvelon.bitbuckler.ui.screen.workspace.WorkspacesTabFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen

@Suppress("TooManyFunctions")
object Screen {

    fun privacyPolicy(type: String = LOAD_TYPE_DEFAULT) = FragmentScreen {
        PrivacyPolicyFragment.newInstance(type)
    }

    fun start() = FragmentScreen("start") {
        StartFragment.newInstance()
    }

    fun main() = FragmentScreen("main") {
        MainFragment.newInstance()
    }

    fun workspaceList(rootTab: String) = FragmentScreen("${rootTab}_workspaces") {
        WorkspaceListFragment()
    }

    fun projectList(workspace: Workspace) = FragmentScreen {
        ProjectListFragment.newInstance(workspace)
    }

    fun repositoryList(workspace: Workspace, project: Project) = FragmentScreen {
        RepositoryListFragment.newInstance(workspace, project)
    }

    fun repositoryDetails(slugs: Slugs) = FragmentScreen {
        RepositoryDetailsFragment.newInstance(slugs)
    }

    fun sourceList(sourceArgs: SourceArgs) = FragmentScreen {
        SourceListFragment.newInstance(sourceArgs)
    }

    fun sourceDetails(sourceDetailsArgs: SourceDetailsArgs) = FragmentScreen {
        SourceDetailsFragment.newInstance(sourceDetailsArgs)
    }

    fun commitList(commitsArgs: CommitsArgs) = FragmentScreen {
        CommitListFragment.newInstance(commitsArgs)
    }

    fun commitDetails(commitDetailsArgs: CommitDetailsArgs) = FragmentScreen {
        CommitDetailsFragment.newInstance(commitDetailsArgs)
    }

    fun pullRequestCommitList(pullRequestArgs: PullRequestArgs) = FragmentScreen {
        PrCommitListFragment.newInstance(pullRequestArgs)
    }

    fun pullRequestList(slugs: Slugs) = FragmentScreen {
        PullRequestListFragment.newInstance(slugs)
    }

    fun branchList(slugs: Slugs) = FragmentScreen {
        BranchListFragment.newInstance(slugs)
    }

    fun issueList(slugs: Slugs) = FragmentScreen {
        IssueListFragment.newInstance(slugs)
    }

    fun issueDetails(issueArgs: IssueArgs) = FragmentScreen {
        IssueDetailsFragment.newInstance(issueArgs)
    }

    fun pullRequestDetails(pullRequestArgs: PullRequestArgs) = FragmentScreen {
        PullRequestDetailsFragment.newInstance(pullRequestArgs)
    }

    fun changes(changesArgs: ChangesArgs) = FragmentScreen {
        ChangesFragment.newInstance(changesArgs)
    }

    fun inlineComments(inlineCommentsArgs: InlineCommentsArgs) = FragmentScreen {
        InlineCommentsFragment.newInstance(inlineCommentsArgs)
    }

    fun activityList(rootTab: String) = FragmentScreen("${rootTab}_activity") {
        ActivityListFragment.newInstance()
    }

    fun account(rootTab: String) = FragmentScreen("${rootTab}_account") {
        AccountFragment.newInstance()
    }

    fun tab(tabTag: String) = FragmentScreen(tabTag) {
        when (tabTag) {
            TabTags.ACTIVITY -> ActivityTabFragment()
            TabTags.WORKSPACES -> WorkspacesTabFragment()
            TabTags.ACCOUNT -> AccountTabFragment()
            else -> throw IllegalArgumentException()
        }
    }

    fun search(workspace: Workspace) = FragmentScreen {
        SearchFragment.newInstance(workspace)
    }

    fun trackRepoSelection() = FragmentScreen {
        TrackRepoSelectionFragment.newInstance()
    }
}
