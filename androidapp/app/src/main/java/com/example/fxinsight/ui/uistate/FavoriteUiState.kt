package com.example.fxinsight.ui.uistate

import com.example.fxinsight.data.network.dto.favorite.response.DeleteAllFavoriteResponse
import com.example.fxinsight.data.network.dto.favorite.response.DeleteFavoriteResponse
import com.example.fxinsight.data.network.dto.favorite.response.FavoriteResponse
import kotlinx.serialization.SerialName

data class FavoriteUiState(
    val favorites: Favorites = Favorites(),
    val favCreate: FavUiState<FavoriteResponse> = FavUiState.Idle,
    val favFetch: FavUiState<List<FavoriteResponse>> = FavUiState.Idle,
    val favDelete: FavUiState<DeleteFavoriteResponse> = FavUiState.Idle,
    val favDeleteAll: FavUiState<DeleteAllFavoriteResponse> = FavUiState.Idle,
    )


data class Favorites(
    val favList: List<FavoriteResponse> = emptyList(),
)
{
    val number: Int
        get() = favList.size
}

sealed interface FavUiState<out T>{
    object Idle: FavUiState<Nothing>

    object Loading: FavUiState<Nothing>

    data class Success<T>(val data: T): FavUiState<T>

    data class Error(val message: String?): FavUiState<Nothing>
}