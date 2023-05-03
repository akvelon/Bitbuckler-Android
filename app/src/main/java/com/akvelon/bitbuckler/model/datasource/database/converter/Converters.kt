/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Roman Gromov (roman.gromov@akvelon.com)
 * on 23 November 2020
 */

package com.akvelon.bitbuckler.model.datasource.database.converter

import androidx.room.TypeConverter
import com.akvelon.bitbuckler.extension.toMap
import com.akvelon.bitbuckler.model.entity.*
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequestState
import com.akvelon.bitbuckler.model.entity.pullrequest.PullRequestType
import com.akvelon.bitbuckler.model.entity.repository.Branch
import com.akvelon.bitbuckler.model.entity.repository.Target
import com.akvelon.bitbuckler.model.entity.repository.issue.IssueKind
import com.akvelon.bitbuckler.model.entity.repository.issue.IssuePriority
import com.akvelon.bitbuckler.model.entity.repository.issue.IssueState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime

class Converters {

    @TypeConverter
    fun fromNameToState(value: String): PullRequestState {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromStateToName(value: PullRequestState): String {
        return value.name
    }

    @TypeConverter
    fun fromNameToType(value: String): PullRequestType {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromTypeToName(value: PullRequestType): String {
        return value.name
    }

    @TypeConverter
    fun fromLinks(value: Links): String {
        return value.avatar.href
    }

    @TypeConverter
    fun toLinks(value: String): Links {
        return Links(Link(value))
    }

    @TypeConverter
    fun toLocalDateTime(value: String): LocalDateTime {
        return LocalDateTime.parse(value)
    }

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime): String {
        return value.toString()
    }

    @TypeConverter
    fun fromProject(value: Project): String {
        return mapOf("uuid" to value.uuid,
            "name" to value.name,
            "isPrivate" to value.isPrivate,
            "key" to value.key,
            "link" to value.links.avatar.href).toString()
    }

    @TypeConverter
    fun toProject(value: String): Project {
        val map = value.toMap()
        return Project(
            uuid = map["uuid"] ?: "",
            name = map["name"] ?: "",
            key = map["key"] ?: "",
            isPrivate = map["isPrivate"]?.toBoolean(),
            links = Links(Link(map["link"] ?: ""))
        )
    }

    @TypeConverter
    fun fromBranch(value: Branch): String {
        return mapOf(
            "name" to value.name,
            "updated_date" to (value.target?.updatedOn?.toString() ?: "")
        ).toString()
    }

    @TypeConverter
    fun toBranch(value: String): Branch {
        val map = value.toMap()
        val updatedDateString = map["updated_date"] ?: ""

        return Branch(
            name = map["name"] ?: "",
            target = if (updatedDateString.isNotBlank()) {
                Target(toLocalDateTime(updatedDateString))
            } else null
        )
    }

    //issue tracker

    @TypeConverter
    fun fromNameToIssuePriority(value: String): IssuePriority {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromIssuePriorityToName(value: IssuePriority): String {
        return value.name
    }

    @TypeConverter
    fun fromNameToIssueKind(value: String): IssueKind {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromIssueKindToName(value: IssueKind): String {
        return value.name
    }

    @TypeConverter
    fun fromNameToIssueState(value: String): IssueState {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromIssueStateToName(value: IssueState): String {
        return value.name
    }

    @TypeConverter
    fun fromStringToUser(value: String?): User? {
        if (value == null)
            return null
        val listType = object : TypeToken<User>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromUserToString(value: User?): String? {
        if (value == null)
            return null
        return Gson().toJson(value)
    }

    @TypeConverter
    fun fromStringToContent(value: String?): Content? {
        if (value == null)
            return null
        val listType = object : TypeToken<Content>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromIssueStateToName(value: Content?): String? {
        if (value == null)
            return null
        return Gson().toJson(value)
    }
}
