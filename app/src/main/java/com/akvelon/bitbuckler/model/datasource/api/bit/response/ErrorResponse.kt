/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 11 March 2021
 */

package com.akvelon.bitbuckler.model.datasource.api.bit.response

import com.akvelon.bitbuckler.model.entity.error.ErrorMessage

class ErrorResponse(

    val type: String,

    val error: ErrorMessage
)
