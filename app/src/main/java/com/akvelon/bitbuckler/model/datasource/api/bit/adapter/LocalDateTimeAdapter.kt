/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 16 December 2020
 */

package com.akvelon.bitbuckler.model.datasource.api.bit.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.lang.IllegalStateException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter : TypeAdapter<LocalDateTime>() {

    override fun write(out: JsonWriter, value: LocalDateTime) {
        out.value(value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
    }

    override fun read(input: JsonReader): LocalDateTime? =
        try {
            LocalDateTime.parse(input.nextString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        } catch (exception: IllegalStateException) {
            input.skipValue()
            null
        }
}
