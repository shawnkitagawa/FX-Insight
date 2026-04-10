package com.example.fxinsight.ui.uistate

data class AuthUiState(
    val authState: AuthState = AuthState.Idle,
    val authInput: AuthInput = AuthInput(),
    val authPage: AuthPage = AuthPage.SignIn,
    val sessionState: SessionState = SessionState.Idle

)
data class AuthInput (
    val email: String = "",
    val password: String = "",
    val userName: String? = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val userNameError: String? = null
)
sealed interface AuthState{
    object Idle: AuthState

    object Loading: AuthState

    object Success: AuthState

    data class Error(val message: String) : AuthState
}

sealed interface SessionState{
    object Idle: SessionState

    object Checking: SessionState

    object Authenticated: SessionState

    object unAutheticated: SessionState
}
sealed interface AuthPage{
    object SignIn: AuthPage

    object signUp: AuthPage
}