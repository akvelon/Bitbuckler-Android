/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 05 February 2021
 */

package com.akvelon.bitbuckler.model.entity.repository.issue

import com.google.gson.annotations.SerializedName

enum class IssueState(val value: String) {

    @SerializedName("new")
    NEW("NEW"),

    @SerializedName("open")
    OPEN("OPEN"),

    @SerializedName("resolved")
    RESOLVED("RESOLVED"),

    @SerializedName("on hold")
    ON_HOLD("ON HOLD"),

    @SerializedName("invalid")
    INVALID("INVALID"),

    @SerializedName("duplicate")
    DUPLICATE("DUPLICATE"),

    @SerializedName("wontfix")
    WONTFIX("WONTFIX"),

    @SerializedName("closed")
    CLOSED("CLOSED")
}
