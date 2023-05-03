/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 19 January 2021
 */

package com.akvelon.bitbuckler.model.entity.comment

import android.os.Parcelable
import com.akvelon.bitbuckler.extension.dropFirstSymbol
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.file.FileDiff
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.AddedLine
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.ConflictedLine
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.LineDiff
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.RemovedLine
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.UnchangedLine
import kotlinx.parcelize.Parcelize

@Parcelize
data class Inline(
    val to: Int?,

    val from: Int?,

    val path: String
) : Parcelable {

    companion object {
        fun fromDiff(file: FileDiff, line: LineDiff?): Inline {
            val path = file.filePath.dropFirstSymbol()

            return when (line) {
                is AddedLine -> Inline(line.newNumber, null, path)
                is RemovedLine -> Inline(null, line.oldNumber, path)
                is UnchangedLine -> Inline(null, line.oldNumber, path)
                is ConflictedLine -> Inline(line.newNumber, null, path)
                else -> Inline(null, null, path)
            }
        }
    }
}
