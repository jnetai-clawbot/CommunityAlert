package com.jnetai.communityalert.data.dao

import androidx.room.*
import com.jnetai.communityalert.data.entity.Alert
import com.jnetai.communityalert.data.entity.AlertCategory
import com.jnetai.communityalert.data.entity.Severity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {

    @Query("SELECT * FROM alerts ORDER BY createdAt DESC")
    fun getAllAlerts(): Flow<List<Alert>>

    @Query("SELECT * FROM alerts WHERE category = :category ORDER BY createdAt DESC")
    fun getAlertsByCategory(category: AlertCategory): Flow<List<Alert>>

    @Query("SELECT * FROM alerts WHERE severity = :severity ORDER BY createdAt DESC")
    fun getAlertsBySeverity(severity: Severity): Flow<List<Alert>>

    @Query("SELECT * FROM alerts WHERE category = :category AND severity = :severity ORDER BY createdAt DESC")
    fun getAlertsByCategoryAndSeverity(category: AlertCategory, severity: Severity): Flow<List<Alert>>

    @Query("SELECT * FROM alerts WHERE date BETWEEN :startDate AND :endDate ORDER BY createdAt DESC")
    fun getAlertsByDateRange(startDate: String, endDate: String): Flow<List<Alert>>

    @Query("SELECT * FROM alerts WHERE acknowledged = 0 AND dismissed = 0 ORDER BY createdAt DESC")
    fun getActiveAlerts(): Flow<List<Alert>>

    @Query("SELECT * FROM alerts WHERE severity = 'CRITICAL' AND acknowledged = 0 ORDER BY createdAt DESC")
    fun getCriticalUnacknowledgedAlerts(): Flow<List<Alert>>

    @Query("SELECT * FROM alerts WHERE id = :id")
    suspend fun getAlertById(id: Long): Alert?

    @Insert
    suspend fun insert(alert: Alert): Long

    @Update
    suspend fun update(alert: Alert)

    @Query("UPDATE alerts SET acknowledged = 1 WHERE id = :id")
    suspend fun acknowledgeAlert(id: Long)

    @Query("UPDATE alerts SET dismissed = 1 WHERE id = :id")
    suspend fun dismissAlert(id: Long)

    @Query("DELETE FROM alerts WHERE id = :id")
    suspend fun deleteAlert(id: Long)

    @Query("SELECT * FROM alerts ORDER BY createdAt DESC")
    suspend fun getAllAlertsSync(): List<Alert>
}