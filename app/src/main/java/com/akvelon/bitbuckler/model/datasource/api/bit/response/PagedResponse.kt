/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 20 November 2020
 */

package com.akvelon.bitbuckler.model.datasource.api.bit.response

open class PagedResponse<T>(
    val page: Int,
    val size: Int,
    val pagelen: Int,
    val values: List<T>,
    val next: String?
) {
    fun <U> copyWithMappingValues(func: (T) -> U) =
        PagedResponse(page, size, pagelen, values.map(func), next)

    val nextPage
        get() = next?.substringAfter("page=")?.substringBefore("&")
}
