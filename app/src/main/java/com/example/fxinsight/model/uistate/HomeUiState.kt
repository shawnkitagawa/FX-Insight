package com.example.fxinsight.model.uistate

data class HomeUiState(
    val homeData: HomeData = HomeData(),
    val favoriteList: List<FavoritePair> = emptyList(),
    val homeState: HomeState = HomeState.Idle

)
data class HomeData(
    val baseAmount: Float? = null,
    val baseCurrency: String = "USD" ,
    val targetAmount: Float? = null,
    val targetCurrency: String = "JPY",
    val number1Icon: Int? = null,
    val number2Icon: Int? = null ,
    val rate: Float? = null,
    val highest: Float? = null,
    val lowest: Float? = null,
    val alert: String? = null,
    val trend:String? = null,
    val dailyChange: Float? = null,
)
data class FavoritePair(
    val fromCurrency: String,
    val toCurrency: String,
)
sealed interface HomeState{
    object Idle: HomeState

    object Loading: HomeState

    object Success: HomeState

    object Error: HomeState
}
