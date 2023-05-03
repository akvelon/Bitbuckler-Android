/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 03 June 2021
 */

package com.akvelon.bitbuckler.di

import com.akvelon.bitbuckler.model.repository.AccountRepository
import com.akvelon.bitbuckler.ui.billing.BillingUseCase
import com.akvelon.bitbuckler.ui.billing.services.BillingService
import com.akvelon.bitbuckler.ui.billing.services.SubscriptionsService
import com.akvelon.bitbuckler.ui.helper.MentionHelper
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val helperModule = module {

    single {
        mentionHelper(get())
    }

    factory {
        BillingService(androidApplication(), get())
    }

    single {
        SubscriptionsService(androidApplication())
    }

    single {
        BillingUseCase(get())
    }
}

fun mentionHelper(accountRepository: AccountRepository) =
    MentionHelper(accountRepository)
