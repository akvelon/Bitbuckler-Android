/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 29 January 2021
 */

package com.akvelon.bitbuckler.model.entity.pullrequest.diff.file

import android.os.Parcelable
import com.akvelon.bitbuckler.extension.dropFirstSymbol
import com.akvelon.bitbuckler.extension.startsWithOneOf
import com.akvelon.bitbuckler.model.entity.TreeNode
import com.akvelon.bitbuckler.model.entity.comment.Comment
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.CommentsHolder
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.AddedLine
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.BinaryLine
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.CollapsedLine
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.ConflictedLine
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.LineDiff
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.RemovedLine
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.line.UnchangedLine
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class FileDiff(

    override val filePath: String,

    var status: FileStatus,

    val lines: List<LineDiff>,

    override val comments: MutableList<TreeNode<Comment>> = mutableListOf()
) : FilePathHolder, CommentsHolder, Parcelable {

    @IgnoredOnParcel
    var outdatedComments = 0

    val isMergeConflict
        get() = status == FileStatus.MERGE_CONFLICT || status == FileStatus.REMOTE_DELETED

    val isLarge
        get() = lines.size > LARGE_DIFF_SIZE

    companion object {
        private const val LARGE_DIFF_SIZE = 300

        private const val ADDED_FILE_STATUS = "new file mode"
        private const val REMOVED_FILE_STATUS = "deleted file mode"
        private const val RENAMED_FILE_STATUS = "similarity index"

        private const val ADDED_FILE_PATH_INDEX = 4
        private const val REMOVED_FILE_PATH_INDEX = 3
        private const val RENAMED_FILE_PATH_INDEX = 3
        private const val MODIFIED_FILE_PATH_INDEX = 2

        private const val ADDED_BINARY_INDEX = 3
        private const val REMOVED_BINARY_INDEX = 3
        private const val RENAMED_BINARY_INDEX = 4
        private const val MODIFIED_BINARY_INDEX = 2

        private const val ADDED_BINARY_PREFIX = "Binary files /dev/null and b"
        private const val ADDED_BINARY_SUFFIX = " differ"
        private const val REMOVED_BINARY_PREFIX = "Binary files a"
        private const val REMOVED_BINARY_SUFFIX = " and /dev/null differ"
        private const val RENAMED_BINARY_PREFIX = "rename to "

        private const val OLD_FILE_PREFIX = "--- a"
        private const val NEW_FILE_PREFIX = "+++ b"
        private const val RENAMED_FILE_PREFIX = "rename to "

        private const val BINARY_FILE_PREFIX = "Binary files"

        private const val DROP_LINES_ADDED = 5
        private const val DROP_LINES_REMOVED = 5
        private const val DROP_LINES_RENAMED = 7
        private const val DROP_LINES_MODIFIED = 4

        private const val NO_NEWLINE_AT_END = "\\ No newline at end of file"

        fun parse(diff: String): FileDiff {
            val diffList = diff.split("\n")

            val status = when {
                diffList[1].startsWith(ADDED_FILE_STATUS) -> FileStatus.ADDED
                diffList[1].startsWith(REMOVED_FILE_STATUS) -> FileStatus.REMOVED
                diffList[1].startsWith(RENAMED_FILE_STATUS) -> FileStatus.RENAMED
                else -> FileStatus.MODIFIED
            }

            return if (isBinaryFile(diffList, status)) {
                parseBinaryFile(diffList, status)
            } else {
                parseSourceFile(diffList, status)
            }
        }

        private fun isBinaryFile(diffList: List<String>, status: FileStatus) =
            when (status) {
                FileStatus.ADDED -> diffList[ADDED_BINARY_INDEX].startsWith(BINARY_FILE_PREFIX)
                FileStatus.REMOVED -> diffList[REMOVED_BINARY_INDEX].startsWith(BINARY_FILE_PREFIX)
                FileStatus.RENAMED ->
                    diffList.elementAtOrNull(RENAMED_BINARY_INDEX)?.startsWith(BINARY_FILE_PREFIX) ?: false
                FileStatus.MODIFIED -> diffList[MODIFIED_BINARY_INDEX].startsWith(BINARY_FILE_PREFIX)
                else -> false
            }

        private fun parseBinaryFile(diffList: List<String>, status: FileStatus): FileDiff {
            val path = when (status) {
                FileStatus.ADDED ->
                    diffList[3]
                        .removePrefix(ADDED_BINARY_PREFIX)
                        .removeSuffix(ADDED_BINARY_SUFFIX)
                FileStatus.REMOVED ->
                    diffList[3]
                        .removePrefix(REMOVED_BINARY_PREFIX)
                        .removeSuffix(REMOVED_BINARY_SUFFIX)
                FileStatus.RENAMED ->
                    diffList[3]
                        .removePrefix(RENAMED_BINARY_PREFIX)
                FileStatus.MODIFIED -> diffList[0].substring(1..(diffList[0].length - 1) / 2).trimEnd()
                else -> ""
            }

            return FileDiff(path, status, listOf(BinaryLine()))
        }

        private fun parseSourceFile(diffList: List<String>, status: FileStatus): FileDiff {
            val path = when (status) {
                FileStatus.ADDED -> diffList[ADDED_FILE_PATH_INDEX].removePrefix(NEW_FILE_PREFIX)
                FileStatus.REMOVED -> diffList[REMOVED_FILE_PATH_INDEX].removePrefix(OLD_FILE_PREFIX)
                FileStatus.RENAMED -> "/%s".format(
                    diffList[RENAMED_FILE_PATH_INDEX].removePrefix(RENAMED_FILE_PREFIX)
                )
                FileStatus.MODIFIED -> diffList[MODIFIED_FILE_PATH_INDEX].removePrefix(
                    OLD_FILE_PREFIX
                )
                else -> ""
            }

            val lines = when (status) {
                FileStatus.ADDED -> getDiffLines(diffList.drop(DROP_LINES_ADDED))
                FileStatus.REMOVED -> getDiffLines(diffList.drop(DROP_LINES_REMOVED))
                FileStatus.RENAMED -> getDiffLines(diffList.drop(DROP_LINES_RENAMED))
                FileStatus.MODIFIED -> getDiffLines(diffList.drop(DROP_LINES_MODIFIED))
                else -> listOf()
            }

            return FileDiff(path, status, lines)
        }

        private fun getDiffLines(diffList: List<String>): List<LineDiff> {
            var oldLineNumber = 0
            var newLineNumber = 0

            var isConflictedSection = false

            return diffList
                .filter { line ->
                    !line.startsWith(NO_NEWLINE_AT_END)
                }
                .map { line ->
                    when {
                        line.startsWithOneOf(LineDiff.CONFLICTED_LINE_PREFIXES) -> {
                            if (line.startsWith(LineDiff.START_CONFLICTED_SECTION)) {
                                isConflictedSection = true
                            } else if (line.startsWith(LineDiff.END_CONFLICTED_SECTION)) {
                                isConflictedSection = false
                            }

                            ConflictedLine(newLineNumber++, line.dropFirstSymbol())
                        }
                        line.startsWith(LineDiff.ADDED_LINE_PREFIX) -> {
                            AddedLine(newLineNumber++, line.dropFirstSymbol(), isConflictedSection)
                        }
                        line.startsWith(LineDiff.REMOVED_LINE_PREFIX) -> {
                            RemovedLine(oldLineNumber++, line.dropFirstSymbol(), isConflictedSection)
                        }
                        line.startsWith(LineDiff.COLLAPSED_LINE_PREFIX) -> {
                            with(CollapsedLine.getLineNumbers(line)) {
                                oldLineNumber = first
                                newLineNumber = second
                            }

                            CollapsedLine(line)
                        }
                        else -> {
                            UnchangedLine(
                                oldLineNumber++,
                                newLineNumber++,
                                line.dropFirstSymbol(),
                                isConflictedSection
                            )
                        }
                    }
                }
                .dropLast(1)
        }
    }
}
