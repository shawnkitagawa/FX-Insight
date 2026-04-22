package com.example.fxinsight.ui.uistate

import com.example.fxinsight.data.network.dto.alert.DirectionStatus
import com.example.fxinsight.data.network.dto.alert.request.AlertCreate
import com.example.fxinsight.data.network.dto.alert.response.AlertResponse


data class AlertUiState(
    val alerts: List<AlertResponse> = emptyList(),
    val alertInputState:  AlertState<AlertCreate> = AlertState.Idle,
    val alertCreateState : AlertState<AlertResponse> = AlertState.Idle,
    val alertDeleteState : AlertState<String> = AlertState.Idle,
    val alertDeleteAllState: AlertState<String> = AlertState.Idle,
    val alertFetchState: AlertState<List<AlertResponse>> = AlertState.Idle


)


sealed interface AlertState<out T>{
    object Idle: AlertState<Nothing>

    object Loading: AlertState<Nothing>

    data class Success<T>(val data: T): AlertState<T>

    data class Error(val message: String?): AlertState<Nothing>
}