/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 03 June 2021
 */

package com.akvelon.bitbuckler.ui.helper

import com.akvelon.bitbuckler.model.repository.AccountRepository

class MentionHelper(
    private val accountRepository: AccountRepository
) {

    suspend fun setMentions(raw: String): String {
        val matches = Regex(BITBUCKET_MENTION_REGEX).findAll(raw)

        var content = raw

        matches.forEach { match ->
            val uuid = match.groupValues.last()

            val currentUser = accountRepository.getCurrentAccount()
            val user = accountRepository.getAccountByUUIDSuspend(uuid)


            val template =
                if (user.uuid == currentUser.uuid) SELF_MENTION_TEMPLATE else MENTION_TEMPLATE
            content.replace(match.value, template.format("\\@${user.displayName}"))
        }

        return content
    }

    companion object {
        const val BITBUCKET_MENTION_REGEX = "\\@\\{(.*?)\\}"

        const val MENTION_TEMPLATE = "@@%s@@"
        const val SELF_MENTION_TEMPLATE = "@@@%s@@@"
    }
}
