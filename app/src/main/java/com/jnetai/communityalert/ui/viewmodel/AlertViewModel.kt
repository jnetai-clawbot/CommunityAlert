package com.jnetai.communityalert.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jnetai.communityalert.data.database.AlertDatabase
import com.jnetai.communityalert.data.entity.Alert
import com.jnetai.communityalert.data.entity.AlertCategory
import com.jnetai.communityalert.data.entity.Severity
import com.jnetai.communityalert.data.repository.AlertRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AlertViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AlertRepository

    private val _filterCategory = MutableStateFlow<AlertCategory?>(null)
    private val _filterSeverity = MutableStateFlow<Severity?>(null)
    private val _filterStartDate = MutableStateFlow<String?>(null)
    private val _filterEndDate = MutableStateFlow<String?>(null)

    val filterCategory: StateFlow<AlertCategory?> = _filterCategory.asStateFlow()
    val filterSeverity: StateFlow<Severity?> = _filterSeverity.asStateFlow()

    val alerts: StateFlow<List<Alert>> = combine(
        _filterCategory,
        _filterSeverity,
        _filterStartDate,
        _filterEndDate
    ) { category, severity, startDate, endDate ->
        FilterState(category, severity, startDate, endDate)
    }.flatMapLatest { filter ->
        when {
            filter.startDate != null && filter.endDate != null ->
                repository.getAlertsByDateRange(filter.startDate, filter.endDate)
            filter.category != null && filter.severity != null ->
                repository.getAlertsByCategoryAndSeverity(filter.category, filter.severity)
            filter.category != null ->
                repository.getAlertsByCategory(filter.category)
            filter.severity != null ->
                repository.getAlertsBySeverity(filter.severity)
            else -> repository.getAllAlerts()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeAlerts: StateFlow<List<Alert>> = repository.getActiveAlerts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val criticalAlerts: StateFlow<List<Alert>> = repository.getCriticalUnacknowledgedAlerts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private data class FilterState(
        val category: AlertCategory?,
        val severity: Severity?,
        val startDate: String?,
        val endDate: String?
    )

    init {
        val dao = AlertDatabase.getDatabase(application).alertDao()
        repository = AlertRepository(dao)
    }

    fun insertAlert(alert: Alert) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAlert(alert)
        }
    }

    fun updateAlert(alert: Alert) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateAlert(alert)
        }
    }

    fun acknowledgeAlert(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.acknowledgeAlert(id)
        }
    }

    fun dismissAlert(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.dismissAlert(id)
        }
    }

    fun deleteAlert(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAlert(id)
        }
    }

    fun setCategoryFilter(category: AlertCategory?) {
        _filterCategory.value = category
    }

    fun setSeverityFilter(severity: Severity?) {
        _filterSeverity.value = severity
    }

    fun setDateFilter(startDate: String?, endDate: String?) {
        _filterStartDate.value = startDate
        _filterEndDate.value = endDate
    }

    fun clearFilters() {
        _filterCategory.value = null
        _filterSeverity.value = null
        _filterStartDate.value = null
        _filterEndDate.value = null
    }

    suspend fun getAllAlertsForExport(): List<Alert> {
        return repository.getAllAlertsSync()
    }
}