package com.example.fxinsight.ui.uistate

import com.example.fxinsight.data.network.dto.history.response.DeleteAllHistoryResponse
import com.example.fxinsight.data.network.dto.history.response.DeleteHistoryResponse
import com.example.fxinsight.data.network.dto.history.response.HistoryResponse

data class HistoryUiState(
    val filter: HistoryFilter = HistoryFilter.ALL,
    val history: List<HistoryResponse> = emptyList(),
    val fetchHistoryState: HistoryState<List<HistoryResponse>> = HistoryState.Idle,
    val deleteHistoryState: HistoryState<DeleteHistoryResponse> = HistoryState.Idle,
    val deleteAllHistoryState: HistoryState<DeleteAllHistoryResponse> = HistoryState.Idle,

    )

enum class HistoryFilter{
    ALL,
    TODAY,
    WEEK,
}

sealed interface HistoryState<out T>{

    object Idle: HistoryState<Nothing>

    object Loading: HistoryState<Nothing>

    data class Success<T>(val data: T): HistoryState<T>

    data class Error(val message: String?): HistoryState<Nothing>
}