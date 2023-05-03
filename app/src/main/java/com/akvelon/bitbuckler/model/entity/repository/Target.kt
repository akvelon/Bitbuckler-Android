/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 03 February 2021
 */

package com.akvelon.bitbuckler.model.entity.repository

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

class Target(
    @SerializedName("date")
    val updatedOn: LocalDateTime
)
