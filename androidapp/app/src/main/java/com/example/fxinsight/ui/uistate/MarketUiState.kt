package com.example.fxinsight.ui.uistate

import com.example.fxinsight.data.network.dto.currency.TimeGroup

data class MarketUiState (
    val MarketInsight: String? = null,
    val selectRange: TimeGroup = TimeGroup.DAY,
    val graphs: Map<TimeGroup, Graph> = emptyMap(),
    val marketState: MarketState = MarketState.Loading,
    val InsightState: MarketState = MarketState.Loading,
    val Insight: String? = null
)

data class Graph(
    val x: List<Long>,
    val y: List<Double>,
)

sealed interface MarketState{

    data object Idle: MarketState

    data object Loading: MarketState

    data object Success: MarketState

    data class Error (val message: String): MarketState
}




