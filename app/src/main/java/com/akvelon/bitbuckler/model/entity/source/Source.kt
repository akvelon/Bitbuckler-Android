/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 29 April 2021
 */

package com.akvelon.bitbuckler.model.entity.source

import com.akvelon.bitbuckler.model.entity.repository.Commit

data class Source(

    val path: String,

    val type: SourceType,

    val commit: Commit
) {
    val name
        get() = path.split("/").last()
}
