/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 24 February 2021
 */

package com.akvelon.bitbuckler.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Slugs(

    val workspace: String,

    val repository: String
) : Parcelable
