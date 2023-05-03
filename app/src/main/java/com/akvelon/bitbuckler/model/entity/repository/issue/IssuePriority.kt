/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 05 February 2021
 */

package com.akvelon.bitbuckler.model.entity.repository.issue

import com.google.gson.annotations.SerializedName

enum class IssuePriority {

    @SerializedName("trivial")
    TRIVIAL,

    @SerializedName("minor")
    MINOR,

    @SerializedName("major")
    MAJOR,

    @SerializedName("critical")
    CRITICAL,

    @SerializedName("blocker")
    BLOCKER
}
