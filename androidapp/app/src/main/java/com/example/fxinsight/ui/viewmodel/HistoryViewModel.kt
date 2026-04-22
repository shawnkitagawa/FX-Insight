package com.example.fxinsight.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fxinsight.application.FXInsightApplication
import com.example.fxinsight.data.repositiory.HistoryRepository
import com.example.fxinsight.ui.uistate.HistoryFilter
import com.example.fxinsight.ui.uistate.HistoryState
import com.example.fxinsight.ui.uistate.HistoryUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HistoryViewModel(private val historyrepository: HistoryRepository): ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState

//    init{
//        fetchHistory()
//    }

    fun fetchHistory() {
        _uiState.value = _uiState.value.copy(
            fetchHistoryState = HistoryState.Loading
        )

        viewModelScope.launch {
            val result = historyrepository.fetchHistories()

            result.fold(
                onSuccess = { response ->
                    // Initially sort by date descending and limit to 50
                    val sortedList = response.sortedByDescending { it.createdAt }.take(50)
                    _uiState.value = _uiState.value.copy(
                        fetchHistoryState = HistoryState.Success(response),
                        history = sortedList
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        fetchHistoryState = HistoryState.Error(message = it.message?: "History fetch failed")
                    )
                },
            )
        }
    }

    fun deleteHistory(id: String ) {
        _uiState.value = _uiState.value.copy(
            deleteHistoryState = HistoryState.Loading
        )

        viewModelScope.launch{
            val result = historyrepository.deleteHistory(id)

            result.fold(
                onSuccess = { response ->
                    _uiState.value = _uiState.value.copy(
                        deleteHistoryState = HistoryState.Success(response),
                        history = _uiState.value.history.filterNot{it.id == id}
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        deleteHistoryState = HistoryState.Error(message = error.message?: "History delete failed")
                    )
                },
            )
        }
    }

    fun deleteAllHistory() {
        _uiState.value = _uiState.value.copy(
            deleteAllHistoryState = HistoryState.Loading
        )
        viewModelScope.launch{
            val result = historyrepository.deleteAllHistories()
            result.fold(
                onSuccess = { response ->
                    _uiState.value = _uiState.value.copy(
                        deleteAllHistoryState = HistoryState.Success(response),
                        history = emptyList()
                    )
                },
                onFailure = {error ->
                    _uiState.value = _uiState.value.copy(
                        deleteAllHistoryState = HistoryState.Error(message = error.message?: "History delete all failed")
                    )
                }
            )
        }
    }

    fun filterHistoryList() {
        viewModelScope.launch {
            // We need to fetch fresh list or use a cached master list to re-filter
            // For now, re-fetching to ensure data integrity
            val result = historyrepository.fetchHistories()
            
            result.onSuccess { response ->
                val historyList = response
                val filter = _uiState.value.filter
                val zone = ZoneId.systemDefault()

                val filtered = when(filter) {
                    HistoryFilter.TODAY -> {
                        historyList.filter { 
                            val itemDate = Instant.parse(it.createdAt).atZone(zone).toLocalDate()
                            itemDate == LocalDate.now(zone)
                        }
                    }
                    HistoryFilter.WEEK -> {
                        val oneWeekAgo = LocalDate.now(zone).minusDays(7)
                        historyList.filter {
                            val itemDate = Instant.parse(it.createdAt).atZone(zone).toLocalDate()
                            !itemDate.isBefore(oneWeekAgo)
                        }
                    }
                    HistoryFilter.ALL -> historyList
                }.sortedByDescending { it.createdAt }.take(50)

                _uiState.value = _uiState.value.copy(history = filtered)
            }
        }
    }

    fun updateHisotryFilter(filter: HistoryFilter) {
        _uiState.value = _uiState.value.copy(filter = filter)
    }

    fun resetFetchHistory() {
        _uiState.value = _uiState.value.copy(fetchHistoryState = HistoryState.Idle)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FXInsightApplication
                val repository = application.container.historyRepository
                HistoryViewModel(repository)
            }
        }
    }
}
