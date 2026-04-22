package com.jnetai.communityalert.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "alerts")
data class Alert(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: AlertCategory,
    val severity: Severity,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val date: LocalDate = LocalDate.now(),
    val acknowledged: Boolean = false,
    val dismissed: Boolean = false,
    val reminderTime: LocalDateTime? = null
)

enum class AlertCategory(val displayName: String) {
    WEATHER("Weather"),
    SAFETY("Safety"),
    TRAFFIC("Traffic"),
    COMMUNITY("Community Events")
}

enum class Severity(val displayName: String) {
    INFO("Info"),
    WARNING("Warning"),
    CRITICAL("Critical")
}