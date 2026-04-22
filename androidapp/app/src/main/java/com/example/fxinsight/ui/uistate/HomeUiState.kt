package com.example.fxinsight.ui.uistate

import com.example.fxinsight.data.network.dto.currency.response.CurrencyInformationResponse
import com.example.fxinsight.data.network.dto.currency.response.DailyChangeResponse
import com.example.fxinsight.data.network.dto.currency.response.WeeklyStatisticsResponse
import com.example.fxinsight.data.network.dto.history.response.HistoryResponse

data class HomeUiState(
    val conversionState: UiState<HistoryResponse> = UiState.Idle,
    val weeklyStatisticState: UiState<WeeklyStatisticsResponse> = UiState.Idle,
    val dailyChangeState: UiState<DailyChangeResponse> = UiState.Idle,
    val baseCurrencyInformationState: UiState<CurrencyInformationResponse> = UiState.Idle,
    val targetCurrencyInformationState: UiState<CurrencyInformationResponse> = UiState.Idle,
    val userInput: UserInput = UserInput(),
    val convertedAmount: Double = 150.23, // Sensible default for USD -> JPY
    val conversionRate: Double = 150.23,
    val conversionCreatedAt: String = "just now"
)

data class UserInput(
    val baseAmount: Double = 1.0,
    val baseCurrency: String = "USD",
    val targetCurrency: String = "JPY"
)

sealed interface UiState<out T> {
    object Idle : UiState<Nothing>
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String?) : UiState<Nothing>
}
