/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 16 December 2020
 */

package com.akvelon.bitbuckler.extension

import android.content.Context
import android.text.format.DateUtils
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.model.entity.TimePeriod
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

private const val SECONDS_IN_MINUTES = 60
private const val SECONDS_IN_HOUR = 3600
private const val SECONDS_IN_DAY = 86400
private const val DAYS_IN_WEEK = 7

fun LocalDateTime.getRelativeDateTime(context: Context): CharSequence =
    DateUtils.getRelativeDateTimeString(
        context,
        this.toInstant(ZoneOffset.UTC).toEpochMilli(),
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.WEEK_IN_MILLIS,
        DateUtils.FORMAT_SHOW_TIME
    )

fun LocalDateTime.timeAgo(context: Context) = this.countTimeAgo(context, short = false)

fun LocalDateTime.timeAgoForRepos(context: Context) =
    this.countTimeAgoForRepos(context, short = false)

fun LocalDateTime.timeAgoShort(context: Context) = this.countTimeAgo(context, short = true)

fun LocalDateTime.getDateFormatted() = "${this.month.value}/${this.dayOfMonth}/${this.year}"

fun LocalDateTime.getDateFormattedForRepos(context: Context): String {
    if (context.resources.configuration.locales[0] == Locale("ru", "RU")) {
        return "${this.dayOfMonth} ${
            this.month.toRussian().capitalizeFirstThreeChars()
        } ${this.year}"
    }
    return "${this.dayOfMonth} ${this.month.toString().capitalizeFirstThreeChars()} ${this.year}"
}

private fun LocalDateTime.countTimeAgo(context: Context, short: Boolean): String {
    val now = LocalDateTime.now(ZoneId.of("UTC"))

    val period = Period.between(this.toLocalDate(), now.toLocalDate())

    return when (true) {
        // Simple date
        period.years > 0, period.months > 0, period.days > DAYS_IN_WEEK ->
            this.getDateFormatted()

        // "N days ago"
        period.days > 1 -> context.getString(
            TimePeriod.DAYS.getStringRes(short),
            period.days.toString()
        )

        // for smaller values
        else -> this.countTimeAgoForDayOrLess(now, period.days, context, short)
    }
}

private fun LocalDateTime.countTimeAgoForRepos(context: Context, short: Boolean): String {
    val now = LocalDateTime.now(ZoneId.of("UTC"))

    val period = Period.between(this.toLocalDate(), now.toLocalDate())

    return when (true) {
        // Simple date
        period.years > 0, period.months > 0, period.days > DAYS_IN_WEEK ->
            this.getDateFormattedForRepos(context)

        // "N days ago"
        period.days > 1 -> context.getString(
            TimePeriod.DAYS.getStringRes(short),
            period.days.toString()
        )

        // for smaller values
        else -> this.countTimeAgoForDayOrLess(now, period.days, context, short)
    }
}

private fun LocalDateTime.countTimeAgoForDayOrLess(
    now: LocalDateTime,
    days: Int,
    context: Context,
    short: Boolean
): String {

    val diff = if (days > 0) {
        now.toSecondsWithDay() - this.toSeconds()
    } else {
        now.toSeconds() - this.toSeconds()
    }

    val daysInDiff = diff / SECONDS_IN_DAY
    val hoursInDiff = diff / SECONDS_IN_HOUR
    val minutesInDiff = diff / SECONDS_IN_MINUTES

    return when (true) {
        // "1d"
        daysInDiff > 0 && short -> context.getString(
            TimePeriod.DAYS.getStringRes(short),
            days.toString()
        )

        // "yesterday"
        daysInDiff > 0 -> context.getString(R.string.yesterday)

        // "N hours ago" or "Nh"
        hoursInDiff > 1, hoursInDiff > 0 && short -> context.getString(
            TimePeriod.HOURS.getStringRes(short),
            hoursInDiff.toString()
        )

        // "an hour ago"
        hoursInDiff > 0 -> context.getString(R.string.an_hour_ago)

        // "N minutes ago" or "Nm"
        minutesInDiff > 1, minutesInDiff > 0 && short -> context.getString(
            TimePeriod.MINUTES.getStringRes(short),
            minutesInDiff.toString()
        )

        // "a minute ago"
        minutesInDiff > 0 -> context.getString(R.string.a_minute_ago)

        // "just now" or "now"
        else -> context.getString(TimePeriod.NOW.getStringRes(short))
    }
}

private fun LocalDateTime.toSeconds() =
    this.second + this.minute * SECONDS_IN_MINUTES + this.hour * SECONDS_IN_HOUR

private fun LocalDateTime.toSecondsWithDay() =
    this.second + this.minute * SECONDS_IN_MINUTES + this.hour * SECONDS_IN_HOUR + SECONDS_IN_DAY
