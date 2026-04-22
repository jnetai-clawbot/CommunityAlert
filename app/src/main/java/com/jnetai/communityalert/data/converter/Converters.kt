package com.jnetai.communityalert.data.converter

import androidx.room.TypeConverter
import com.jnetai.communityalert.data.entity.AlertCategory
import com.jnetai.communityalert.data.entity.Severity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(dateTimeFormatter)
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, dateTimeFormatter) }
    }

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.format(dateFormatter)
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it, dateFormatter) }
    }

    @TypeConverter
    fun fromAlertCategory(value: AlertCategory?): String? {
        return value?.name
    }

    @TypeConverter
    fun toAlertCategory(value: String?): AlertCategory? {
        return value?.let { AlertCategory.valueOf(it) }
    }

    @TypeConverter
    fun fromSeverity(value: Severity?): String? {
        return value?.name
    }

    @TypeConverter
    fun toSeverity(value: String?): Severity? {
        return value?.let { Severity.valueOf(it) }
    }
}