package com.example.fxinsight.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fxinsight.application.FXInsightApplication
import com.example.fxinsight.data.datasource.availableCurrencyList
import com.example.fxinsight.data.network.dto.favorite.request.FavoriteCreate
import com.example.fxinsight.data.repositiory.FavoriteRepository
import com.example.fxinsight.ui.uistate.FavUiState
import com.example.fxinsight.ui.uistate.FavoriteUiState
import com.example.fxinsight.ui.uistate.Favorites
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(private val favoriteRepository: FavoriteRepository): ViewModel() {


    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState>  = _uiState

    private val availableCurrency = availableCurrencyList

    init {
        fetchFavorites()
        resetFavFetch()
        Log.d("init", "${_uiState.value.favorites}")
    }

    fun createFavorite(baseCurrency: String, targetCurrency: String)
    {
        Log.d("createFavorite", "${baseCurrency} ,  ${targetCurrency}")

        if (baseCurrency == null || targetCurrency == null ||
            availableCurrency.none {it.code == baseCurrency}
            || availableCurrency.none {it.code == targetCurrency})
        {
            _uiState.value = _uiState.value.copy(
                favCreate = FavUiState.Error(message = "Invalid Currency")
            )
            Log.d("createFavorite", "Invalid Currency")
            return
        }
        else if (_uiState.value.favorites.number > 5)
        {
            _uiState.value = _uiState.value.copy(
                favCreate = FavUiState.Error(message = "Maximum favorites reached")
            )
            Log.d("createFavorite", "Maximum favorites reached")
            return
        }
        _uiState.value = _uiState.value.copy(
            favCreate = FavUiState.Loading
        )

        val request = FavoriteCreate(
            baseCurrency = baseCurrency,
            targetCurrency = targetCurrency

        )
        viewModelScope.launch {
            Log.d("createFavorite", "Start")
            val result = favoriteRepository.createFavorite(request)
            result.fold(
                onSuccess = { response ->
                    _uiState.value = _uiState.value.copy(
                        favCreate = FavUiState.Success(response),
                        favorites = _uiState.value.favorites.copy(
                            favList = (_uiState.value.favorites.favList + response).distinctBy {
                                it.baseCurrency + it.targetCurrency
                            }
                        )

                    )
                    Log.d("createFavorite", "Succesful")
                    Log.d("createFavorite", "${_uiState.value.favorites}")
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        favCreate = FavUiState.Error(message = error.message?: "Favorite creation failed")
                    )
                    Log.d("createFavorite", " ${error.message}")

                }
                    )
        }
    }

    fun fetchFavorites()
    {
        _uiState.value = _uiState.value.copy(
            favFetch = FavUiState.Loading
        )
        viewModelScope.launch {
            val result = favoriteRepository.fetchFavorites()
            result.fold(
                    onSuccess = { response ->
                        Log.d("fetchFavorites", "Success${response}")
                        _uiState.value = _uiState.value.copy(
                            favFetch = FavUiState.Success(response),
                            favorites = _uiState.value.favorites.copy(
                                favList = response.distinctBy { it.id }
                            )
                        )},
                    onFailure = {error ->
                        Log.d("fetchFavorites", "Error${error.message}")
                        _uiState.value = _uiState.value.copy(
                            favFetch = FavUiState.Error(message = error.message?: "Favorite fetch failed")
                            )
                        Log.d("fetchFavorites", "Error${_uiState.value.favFetch}")
                        }

                    )
                }
            }


    fun deleteFavorite( id: String )
    {
        Log.d("deleteFavorite", "Start")
        _uiState.value = _uiState.value.copy(
            favDelete = FavUiState.Loading
        )
        viewModelScope.launch{
            val result = favoriteRepository.deleteFavorite(id)

            result.fold(
                onSuccess = { response ->
                    Log.d("deleteFavorite", "Succesful")
                    _uiState.value = _uiState.value.copy(
                        favDelete = FavUiState.Success(response),
                        favorites = _uiState.value.favorites.copy(
                            favList = _uiState.value.favorites.favList.filter { it.id != id }
                        )
                    )
                },
                onFailure = { error ->
                    Log.d("deleteFavorite", "Error ${error.message}")
                    _uiState.value = _uiState.value.copy(
                        favDelete = FavUiState.Error(message = error.message?: "Favorite delete failed")
                    )
                }
            )
        }
    }

    fun deleteAllFavorites()
    {
        _uiState.value = _uiState.value.copy(
            favDeleteAll = FavUiState.Loading
        )
        viewModelScope.launch{
            val result = favoriteRepository.deleteAllFavorites()

            result.fold(
                onSuccess = { response ->
                    _uiState.value = _uiState.value.copy(
                        favDeleteAll = FavUiState.Success(response),
                        favorites = Favorites()
                    )

                },
                onFailure = {error ->
                    _uiState.value = _uiState.value.copy(
                        favDeleteAll = FavUiState.Error(message = error.message?: "Favorite delete all failed")

                    )
                },
            )
        }
    }
    fun resetFavCreate()
    {
        _uiState.value = _uiState.value.copy(
            favCreate = FavUiState.Idle
        )

    }

    fun resetFavFetch()
    {
        _uiState.value = _uiState.value.copy(
            favFetch = FavUiState.Idle
        )
    }

    fun resetFavDelete()
    {
        _uiState.value = _uiState.value.copy(
            favDelete = FavUiState.Idle
        )
    }

    fun resetFavDeleteAll()
    {
        _uiState.value = _uiState.value.copy(
            favDeleteAll = FavUiState.Idle
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FXInsightApplication
                val repository = application.container.favoriteRepository
                FavoriteViewModel(favoriteRepository  = repository)
            }
        }
    }

}