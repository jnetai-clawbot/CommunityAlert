package com.jnetai.communityalert.util

import android.content.Context
import com.jnetai.communityalert.data.entity.Alert
import com.jnetai.communityalert.data.entity.AlertCategory
import com.jnetai.communityalert.data.entity.Severity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object AlertExporter {

    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    private fun createGson(): Gson {
        return GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime::class.java, object : TypeAdapter<LocalDateTime>() {
                override fun write(out: JsonWriter, value: LocalDateTime?) {
                    out.value(value?.format(dateTimeFormatter))
                }
                override fun read(reader: JsonReader): LocalDateTime? {
                    return LocalDateTime.parse(reader.nextString(), dateTimeFormatter)
                }
            })
            .registerTypeAdapter(LocalDate::class.java, object : TypeAdapter<LocalDate>() {
                override fun write(out: JsonWriter, value: LocalDate?) {
                    out.value(value?.format(dateFormatter))
                }
                override fun read(reader: JsonReader): LocalDate? {
                    return LocalDate.parse(reader.nextString(), dateFormatter)
                }
            })
            .create()
    }

    fun exportToJson(alerts: List<Alert>): String {
        return createGson().toJson(alerts)
    }
}