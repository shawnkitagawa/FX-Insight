package com.example.fxinsight.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fxinsight.application.FXInsightApplication
import com.example.fxinsight.data.repositiory.ProfileRepository
import com.example.fxinsight.data.repository.FXInsightRepository
import com.example.fxinsight.ui.uistate.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel(private val repository: ProfileRepository): ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState



















    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FXInsightApplication
                val repository = application.container.profileRepository
                ProfileViewModel(repository)
            }
        }
    }
}