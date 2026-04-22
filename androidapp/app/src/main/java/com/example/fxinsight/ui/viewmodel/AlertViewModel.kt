package com.example.fxinsight.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fxinsight.application.FXInsightApplication
import com.example.fxinsight.data.network.dto.alert.DirectionStatus
import com.example.fxinsight.data.network.dto.alert.request.AlertCreate
import com.example.fxinsight.data.repositiory.AlertRepository
import com.example.fxinsight.ui.uistate.AlertState
import com.example.fxinsight.ui.uistate.AlertUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlertViewModel(private val alertRepository: AlertRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AlertUiState())
    val uiState: StateFlow<AlertUiState> = _uiState.asStateFlow()

    init {
        fetchAlerts()
    }

    fun fetchAlerts() {
        viewModelScope.launch {
            Log.d("fetchAlert", "Start fetchAlerts")
            _uiState.value = _uiState.value.copy(alertFetchState = AlertState.Loading)
            val result = alertRepository.fetchAlerts()
            result.fold(
                onSuccess = { responseList ->
                    Log.d("fetchAlert", "Successful ${responseList}")
                    _uiState.value = _uiState.value.copy(
                        alerts = responseList,
                        alertFetchState = AlertState.Success(responseList)
                    )
                },
                onFailure = { error ->
                    Log.d("fetchAlert", "Failed ${error.message}")
                    _uiState.value = _uiState.value.copy(
                        alertFetchState = AlertState.Error(error.message)
                    )
                }
            )
        }
    }

    fun createAlert(base: String, target: String, price: Double, direction: DirectionStatus) {
        viewModelScope.launch {
            Log.d("createAlert", "Start createAlert")
            _uiState.value = _uiState.value.copy(alertCreateState = AlertState.Loading)
            val request = AlertCreate(
                alertTarget = price,
                direction = direction,
                baseCurrency = base,
                targetCurrency = target
            )
            val result = alertRepository.createAlert(request)
            result.fold(
                onSuccess = {
                    Log.d("createAlert", "Succesful createAlert")
                    _uiState.value = _uiState.value.copy(
                        alertCreateState = AlertState.Success(it)
                    )
                    fetchAlerts()
                },
                onFailure = { error ->
                    Log.d("createAlert", "failed ${error.message}")
                    _uiState.value = _uiState.value.copy(
                        alertCreateState = AlertState.Error(error.message)
                    )
                }
            )
        }
    }

    fun deleteAlert(alertId: String) {
        Log.d("DeleteAlert", "Start delete")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(alertDeleteState = AlertState.Loading)

            val result = alertRepository.deleteAlert(alertId)
            result.fold(
                onSuccess = { response ->
                    Log.d("DeleteAlert", "Succesfull")
                    _uiState.value = _uiState.value.copy(
                        alertDeleteState = AlertState.Success(response.message),
                        alerts = _uiState.value.alerts.filter{it.id != alertId}
                    )

                },
                onFailure = { error ->
                    Log.d("DeleteAlert", "failed: ${error.message}")
                    _uiState.value = _uiState.value.copy(
                        alertDeleteState = AlertState.Error(error.message)
                    )
                }
            )
        }
    }

    fun deleteAllAlerts() {
        viewModelScope.launch {
            Log.d("DeleteAll", "Start delete")
            _uiState.value = _uiState.value.copy(
                alertDeleteAllState = AlertState.Loading
            )
            val result = alertRepository.deleteAllAlerts()

            result.fold(
                onSuccess = { response ->
                    Log.d("DeleteAll", "Succesfull")
                    _uiState.value = _uiState.value.copy(
                        alertDeleteAllState = AlertState.Success(response.message),
                        alerts = emptyList()
                    )
                },
                onFailure = { error ->
                    Log.d("DeleteAll", "failed: ${error.message}")
                    _uiState.value = _uiState.value.copy(
                        alertDeleteAllState = AlertState.Error(error.message)
                    )
                },

            )
        }
    }

    fun resetAlertFetch()
    {
        _uiState.value = _uiState.value.copy(
            alertFetchState = AlertState.Idle
        )
    }

    fun resetAlertCreate()
    {
        _uiState.value = _uiState.value.copy(
            alertCreateState = AlertState.Idle
        )
    }

    fun resetAlertDelete()
    {
        _uiState.value = _uiState.value.copy(
            alertDeleteState = AlertState.Idle
        )
    }

    fun resetAlertDeleteAll()
    {
        _uiState.value = _uiState.value.copy(
            alertDeleteAllState = AlertState.Idle
        )

    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FXInsightApplication
                val alertRepository = application.container.alertRepository
                AlertViewModel(alertRepository = alertRepository)
            }
        }
    }
}
