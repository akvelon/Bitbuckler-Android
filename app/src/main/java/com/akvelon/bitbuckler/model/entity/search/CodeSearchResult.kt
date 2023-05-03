/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Alexander Vinogradov (a.vinogradov@akvelon.com)
 * on 05 December 2021
 */

package com.akvelon.bitbuckler.model.entity.search

import com.google.gson.annotations.SerializedName

class CodeSearchResult(

    val type: String,

    @SerializedName("content_match_count")
    val contentMatchCount: Int,

    @SerializedName("content_matches")
    val contentMatches: List<SearchContentMatch>,

    @SerializedName("path_matches")
    val pathMatches: List<SearchSegment>,

    val file: File
) {

    // Merges the matches in the content if the end of one matches the beginning of the next.
    fun copyWithMergedContentMatches(): CodeSearchResult {
        val matches = if (contentMatches.isEmpty()) {
            mutableListOf()
        } else {
            mutableListOf(contentMatches.first())
        }

        for (i in 1 until contentMatches.size) {
            if (matches.last().lines.last().line == contentMatches[i].lines.first().line) {
                val newMatch = joinContentMatches(
                    matches.last(),
                    contentMatches[i]
                )
                matches.removeAt(matches.size - 1)
                matches.add(newMatch)
            }
        }

        return CodeSearchResult(
            type = type,
            contentMatchCount = contentMatchCount,
            contentMatches = matches,
            pathMatches = pathMatches,
            file = file
        )
    }

    private fun joinContentMatches(
        first: SearchContentMatch,
        second: SearchContentMatch
    ): SearchContentMatch {
        val lines = mutableListOf<SearchLine>()

        lines.addAll(first.lines.subList(0, first.lines.size - 1))

        val segments = mutableListOf<SearchSegment>()
        segments.addAll(first.lines.last().segments)
        segments.addAll(second.lines.first().segments)
        lines.add(SearchLine(second.lines.first().line, segments))

        lines.addAll(second.lines.subList(1, second.lines.size))

        return SearchContentMatch(lines)
    }
}
