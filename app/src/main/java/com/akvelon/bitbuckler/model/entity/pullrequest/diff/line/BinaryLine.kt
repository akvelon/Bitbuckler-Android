/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 01 February 2021
 */

package com.akvelon.bitbuckler.model.entity.pullrequest.diff.line

import kotlinx.parcelize.Parcelize

@Parcelize
class BinaryLine : LineDiff(EMPTY_CONTENT) {

    override fun getMaxLineNumber() = 0

    override fun oldNumberToString(length: Int) = " ".repeat(length)

    override fun newNumberToString(length: Int) = " ".repeat(length)
}
