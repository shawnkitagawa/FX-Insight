package com.example.fxinsight.ui.uistate

data class HistoryUiState(
    val filter: Filter = Filter.ALL,
    val history: List<History> = emptyList(),
    val historyState: HistoryState = HistoryState.Loading

)

data class History(
    val currencyIcon: Int,
    val currencyPair: CurrencyPair,
    val currencyAmountPair: CurrencyAmountPair,
    )

data class CurrencyPair(
    val fromCurrency: String,
    val toCurrency: String,
)
data class CurrencyAmountPair(
    val fromAmount: Float,
    val toCurrency: Float,
)

enum class Filter{
    ALL,
    TODAY,
    WEEK,
}

sealed interface HistoryState{

    object Loading: HistoryState

    object Success: HistoryState

    object Error: HistoryState
}