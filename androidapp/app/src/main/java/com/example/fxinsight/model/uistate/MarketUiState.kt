package com.example.fxinsight.model.uistate

import java.sql.Timestamp

data class MarketUiState (
    val MarketInsight: String? = null,
    val selectRange: TimeRange = TimeRange.DAY,
    val graphs: Map<TimeRange, Graph> = emptyMap(),
    val marketState: MarketState = MarketState.Loading

)

data class Graph(
    val x: List<Timestamp>,
    val y: List<Float>,
)

enum class TimeRange{
    DAY,
    WEEK,
    MONTH,
    HALF_YEAR,
    YEAR,
}

sealed interface MarketState{

    data object Loading: MarketState

    data object Success: MarketState

    data class Error (val message: String): MarketState
}




