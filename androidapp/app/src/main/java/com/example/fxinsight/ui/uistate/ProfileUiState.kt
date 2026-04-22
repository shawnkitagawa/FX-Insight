package com.example.fxinsight.ui.uistate

data class ProfileUiState (
    val profileState: ProfileState = ProfileState.Idle,
    val userName: String = "",
    val email: String = "",
    val avatarUrl: String? = null
)

sealed interface ProfileState{
    object Idle: ProfileState
    object Loading: ProfileState
    object Success: ProfileState
    data class Error(val message: String): ProfileState
}
