package com.example.fxinsight.ui.uistate

data class ProfileUiState (
    val profileState: ProfileState = ProfileState.Idle
)


sealed interface ProfileState{

    object Idle: ProfileState

    object Loading: ProfileState

    object Success: ProfileState

    data class Error(val message: String): ProfileState
}
