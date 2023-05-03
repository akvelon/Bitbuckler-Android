/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 24 May 2021
 */

package com.akvelon.bitbuckler.model.entity.args

enum class CommentScope(val slug: String) {

    PULL_REQUESTS("pullrequests"),
    ISSUES("issues"),
    COMMITS("commit")
}
