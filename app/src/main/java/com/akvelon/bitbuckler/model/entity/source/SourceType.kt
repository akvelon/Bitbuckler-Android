/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 29 April 2021
 */

package com.akvelon.bitbuckler.model.entity.source

import com.google.gson.annotations.SerializedName

enum class SourceType {

    @SerializedName("commit_directory")
    COMMIT_DIRECTORY,

    @SerializedName("commit_file")
    COMMIT_FILE
}
