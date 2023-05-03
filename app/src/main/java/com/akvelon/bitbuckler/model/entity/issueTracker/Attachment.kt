/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 19 January 2021
 */

package com.akvelon.bitbuckler.model.entity.issueTracker

import com.akvelon.bitbuckler.model.entity.issueTracker.link.LinksArray

data class Attachment(
    val name: String,
    val links: LinksArray,
    val type: String,
)