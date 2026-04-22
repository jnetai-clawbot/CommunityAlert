package com.jnetai.communityalert.data.repository

import com.jnetai.communityalert.data.dao.AlertDao
import com.jnetai.communityalert.data.entity.Alert
import com.jnetai.communityalert.data.entity.AlertCategory
import com.jnetai.communityalert.data.entity.Severity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AlertRepository(private val alertDao: AlertDao) {

    fun getAllAlerts(): Flow<List<Alert>> = alertDao.getAllAlerts()

    fun getAlertsByCategory(category: AlertCategory): Flow<List<Alert>> =
        alertDao.getAlertsByCategory(category)

    fun getAlertsBySeverity(severity: Severity): Flow<List<Alert>> =
        alertDao.getAlertsBySeverity(severity)

    fun getAlertsByCategoryAndSeverity(category: AlertCategory, severity: Severity): Flow<List<Alert>> =
        alertDao.getAlertsByCategoryAndSeverity(category, severity)

    fun getAlertsByDateRange(startDate: String, endDate: String): Flow<List<Alert>> =
        alertDao.getAlertsByDateRange(startDate, endDate)

    fun getActiveAlerts(): Flow<List<Alert>> = alertDao.getActiveAlerts()

    fun getCriticalUnacknowledgedAlerts(): Flow<List<Alert>> = alertDao.getCriticalUnacknowledgedAlerts()

    suspend fun getAlertById(id: Long): Alert? = withContext(Dispatchers.IO) {
        alertDao.getAlertById(id)
    }

    suspend fun insertAlert(alert: Alert): Long = withContext(Dispatchers.IO) {
        alertDao.insert(alert)
    }

    suspend fun updateAlert(alert: Alert) = withContext(Dispatchers.IO) {
        alertDao.update(alert)
    }

    suspend fun acknowledgeAlert(id: Long) = withContext(Dispatchers.IO) {
        alertDao.acknowledgeAlert(id)
    }

    suspend fun dismissAlert(id: Long) = withContext(Dispatchers.IO) {
        alertDao.dismissAlert(id)
    }

    suspend fun deleteAlert(id: Long) = withContext(Dispatchers.IO) {
        alertDao.deleteAlert(id)
    }

    suspend fun getAllAlertsSync(): List<Alert> = withContext(Dispatchers.IO) {
        alertDao.getAllAlertsSync()
    }
}