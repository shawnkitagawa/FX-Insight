package com.example.fxinsight.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fxinsight.application.FXInsightApplication
import com.example.fxinsight.data.network.dto.currency.TimeGroup
import com.example.fxinsight.data.repositiory.CurrencyRepository
import com.example.fxinsight.data.repositiory.InsightRepository
import com.example.fxinsight.ui.uistate.Graph
import com.example.fxinsight.ui.uistate.MarketState
import com.example.fxinsight.ui.uistate.MarketUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset


class MarketViewModel(private val repository: CurrencyRepository,
    private val insightRepository: InsightRepository
): ViewModel() {


    private val _uiState = MutableStateFlow(MarketUiState())
    val uiState: StateFlow<MarketUiState> = _uiState



    fun getAiInsight(base: String, target: String)
    {
        viewModelScope.launch {
            Log.d("getAiInsight", "Start")

            val results = insightRepository.getAiInsight(base, target)

            results.fold(
                onSuccess = { response ->
                    Log.d("getAiInsight", "Success")
                    _uiState.value = _uiState.value.copy(
                        Insight = response.insight,
                        InsightState = MarketState.Success
                    )
                },
                onFailure = {error ->
                    Log.d("getAiInsight", "Failure ${error.message}")
                    _uiState.value = _uiState.value.copy(
                        InsightState = MarketState.Error(message = error.message?: "Ai insight failed to fetch")
                    )
                },

            )



        }

    }



    fun getMarketData(baseCurrency: String, targetCurrency: String)
    {
        Log.d("getmarketData", "Start")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                marketState = MarketState.Loading
            )

            val result = repository.getHistoricalGraph(baseCurrency,
                targetCurrency, _uiState.value.selectRange.name.lowercase())
            Log.d("getmarketData", "SelectRange: ${_uiState.value.selectRange}")
            result.fold(
                onSuccess = { historicalGraphResponseList ->
                    Log.d("getmarketData", "historicalGraphResponseList: $historicalGraphResponseList")

                    val timestamps = historicalGraphResponseList.map{
                        LocalDate.parse(it.date)
                            .atTime(16, 0) // ECB publish time
                            .toEpochSecond(ZoneOffset.ofHours(1))
                    }

                    val rates = historicalGraphResponseList.map{
                        it.rate
                    }

                    val graph = Graph(
                        x = timestamps,
                        y = rates
                    )

                    val updatedGraphs = _uiState.value.graphs.toMutableMap()
                    updatedGraphs[_uiState.value.selectRange] = graph

                    _uiState.value = _uiState.value.copy(
                        graphs = updatedGraphs,
                        marketState = MarketState.Success
                    )

                },
                onFailure = {error ->
                    Log.d("getmarketData", "error: $error")
                    _uiState.value = _uiState.value.copy(
                        marketState = MarketState.Error(message = error.message?: "market data failed to fetch")
                    )

                },
            )

        }

    }

    fun updateTimeGroup(timegroup: TimeGroup)
    {
        _uiState.value = _uiState.value.copy(
            selectRange = timegroup
        )
    }

    fun ResetGraph()
    {
        _uiState.value = _uiState.value.copy(
            graphs = emptyMap()
        )
    }

    fun ResetMarketState()
    {
        _uiState.value = _uiState.value.copy(
            marketState = MarketState.Idle
        )
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FXInsightApplication
                val repository = application.container.currencyRepository
                val insightRepository = application.container.insightRepository
                MarketViewModel(repository, insightRepository)
            }
        }
    }
}