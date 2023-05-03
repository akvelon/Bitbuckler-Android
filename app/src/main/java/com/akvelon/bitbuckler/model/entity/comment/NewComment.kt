/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 25 January 2021
 */

package com.akvelon.bitbuckler.model.entity.comment

import com.akvelon.bitbuckler.model.entity.NewContent

class NewComment(

    val content: NewContent,

    val parent: Parent?
) {

    constructor(content: NewContent) : this(content, null)

    var inline: Inline? = null
}
