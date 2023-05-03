/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 07 December 2020
 */

package com.akvelon.bitbuckler.di

import com.akvelon.bitbuckler.model.repository.*
import com.akvelon.bitbuckler.model.repository.local.BitbucklerCachedReposRepository
import com.akvelon.bitbuckler.model.repository.local.CachedReposRepository
import com.akvelon.bitbuckler.domain.repo.BitbucklerCachedRepos
import com.akvelon.bitbuckler.domain.repo.CachedReposUseCase
import com.akvelon.bitbuckler.model.repository.pullrequest.BitbucklerTrackedPullRequestsRepo
import com.akvelon.bitbuckler.model.repository.pullrequest.TrackedIssuesRepo
import com.akvelon.bitbuckler.model.repository.pullrequest.TrackedIssuesRepoImpl
import com.akvelon.bitbuckler.model.repository.pullrequest.TrackedPullRequestsRepo
import org.koin.dsl.module

val repositoryModule = module {

    single {
        AuthRepository(get(), get())
    }

    single {
        SettingsRepository(get())
    }

    single {
        WorkspaceRepository(get(), get(), get())
    }

    single {
        ProjectRepository(get())
    }

    single {
        RepoRepository(get(), get(), get())
    }

    single {
        PullRequestRepository(get())
    }

    single {
        PullRequestActionsRepository(get())
    }

    single {
        BranchRepository(get())
    }

    single {
        IssuesRepository(get())
    }

    single {
        ActivityRepository(get())
    }

    single {
        AccountRepository(get(), get(), get())
    }

    single {
        SourceRepository(get())
    }

    single {
        CommentRepository(get())
    }

    single {
        CommitDetailsRepository(get())
    }

    single {
        ChangesRepository(get())
    }

    single {
        WorkspaceSizeRepository(get(), get(), get(), get())
    }

    single {
        PrivacyPolicyRepo(get())
    }

    single<CachedReposRepository> {
        BitbucklerCachedReposRepository(get())
    }

    single<TrackedPullRequestsRepo> {
        BitbucklerTrackedPullRequestsRepo(get())
    }

    single<TrackedIssuesRepo> {
        TrackedIssuesRepoImpl(get())
    }

    single<CachedReposUseCase> {
        BitbucklerCachedRepos(get(), get(), get(), get(), get())
    }
}
