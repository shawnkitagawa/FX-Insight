package com.example.fxinsight.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fxinsight.application.FXInsightApplication
import com.example.fxinsight.data.network.dto.profile.request.ProfileCreate
import com.example.fxinsight.data.repositiory.ProfileRepository
import com.example.fxinsight.ui.uistate.ProfileState
import com.example.fxinsight.ui.uistate.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepository): ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init{
        fetchProfile()
    }

    fun createProfile(userName: ProfileCreate)
    {
        viewModelScope.launch{
            _uiState.value = _uiState.value.copy(
                profileState = ProfileState.Loading
            )

            val results = repository.createProfile(userName)

            results.fold(
                onSuccess = { profileresponse ->
                    val name = userName.userName
                    _uiState.value = _uiState.value.copy(
                        profileState = ProfileState.Success,
                        userName = name
                    )
                    Log.d("createProfile", "profile creation was successful ${profileresponse}")
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        profileState = ProfileState.Error(message = error.message?: "Failed to Create Profile")
                    )
                }
            )
        }
    }

    fun  fetchProfile()
    {
        viewModelScope.launch{
            _uiState.value = _uiState.value.copy(
                profileState = ProfileState.Loading
            )
            val results = repository.fetchProfile()

            results.fold(
                onSuccess = { profileResponse ->
                    _uiState.value = _uiState.value.copy(
                        profileState = ProfileState.Success,
                        userName = profileResponse.userName,
                    )
                },
                onFailure = {error ->
                    _uiState.value = _uiState.value.copy(
                        profileState = ProfileState.Error(message = error.message?: "Failed to Fetch Profile")
                    )
                }
            )
        }
    }

    fun deleteProfile()
    {
        viewModelScope.launch{
            _uiState.value = _uiState.value.copy(
                profileState = ProfileState.Loading
            )

            val results = repository.deleteProfile()

            results.fold(
                onSuccess = { deleteProfileResponse ->
                    _uiState.value = _uiState.value.copy(
                        profileState = ProfileState.Success
                    )
                    Log.d("deleteProfile", "${deleteProfileResponse}")
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        profileState = ProfileState.Error(message = error.message?:"Failed to delete Profile")
                    )
                }
            )
        }
    }

    fun resetProfileState()
    {
        _uiState.value = _uiState.value.copy(
            profileState = ProfileState.Idle
        )
    }


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