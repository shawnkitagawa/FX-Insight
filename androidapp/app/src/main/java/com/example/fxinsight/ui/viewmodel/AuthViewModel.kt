package com.example.fxinsight.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fxinsight.application.FXInsightApplication
import com.example.fxinsight.data.repository.FXInsightRepository
import com.example.fxinsight.model.uistate.AuthInput
import com.example.fxinsight.model.uistate.AuthPage
import com.example.fxinsight.model.uistate.AuthState
import com.example.fxinsight.model.uistate.AuthUiState
import com.example.fxinsight.model.uistate.SessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: FXInsightRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun clickWelcomeButton() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(sessionState = SessionState.Checking)

            _uiState.value = _uiState.value.copy(
                sessionState = if (repository.isSession()) {
                    SessionState.Authenticated
                } else {
                    SessionState.unAutheticated
                }
            )
        }
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            authState = AuthState.Idle,
            authInput = _uiState.value.authInput.copy(
                email = email,
                emailError = null
            )
        )
    }

    fun updateUserName(userName: String?) {
        _uiState.value = _uiState.value.copy(
            authState = AuthState.Idle,
            authInput = _uiState.value.authInput.copy(
                userName = userName,
                userNameError = null
            )
        )
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            authState = AuthState.Idle,
            authInput = _uiState.value.authInput.copy(
                password = password,
                passwordError = null
            )
        )
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            clearInputErrors()
            _uiState.value = _uiState.value.copy(authState = AuthState.Loading)

            if (!verifyInput(email, password, null)) {
                _uiState.value = _uiState.value.copy(
                    authState = AuthState.Error("Please fix the input errors")
                )
                return@launch
            }

            repository.signIn(email, password).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(authState = AuthState.Success)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        authState = AuthState.Error(
                            error.message ?: "Something went wrong during sign in"
                        )
                    )
                }
            )
        }
    }

    fun signUp(email: String, password: String, userName: String) {
        viewModelScope.launch {
            clearInputErrors()
            _uiState.value = _uiState.value.copy(authState = AuthState.Loading)

            if (!verifyInput(email, password, userName)) {
                _uiState.value = _uiState.value.copy(
                    authState = AuthState.Error("Please fix the input errors")
                )
                return@launch
            }

            repository.signUp(email, password, userName).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(authState = AuthState.Success)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        authState = AuthState.Error(
                            error.message ?: "Something went wrong during sign up"
                        )
                    )
                }
            )
        }
    }

    private fun verifyInput(email: String, password: String, userName: String?): Boolean {
        var valid = true

        if (!isValidEmail(email)) {
            _uiState.value = _uiState.value.copy(
                authInput = _uiState.value.authInput.copy(
                    emailError = "Please enter a valid email address"
                )
            )
            valid = false
        }

        if (password.length < 8) {
            _uiState.value = _uiState.value.copy(
                authInput = _uiState.value.authInput.copy(
                    passwordError = "Password must be at least 8 characters"
                )
            )
            valid = false
        }

        if (_uiState.value.authPage == AuthPage.signUp && userName.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                authInput = _uiState.value.authInput.copy(
                    userNameError = "Please enter a username"
                )
            )
            valid = false
        }

        return valid
    }

    private fun clearInputErrors() {
        _uiState.value = _uiState.value.copy(
            authInput = _uiState.value.authInput.copy(
                emailError = null,
                passwordError = null,
                userNameError = null
            )
        )
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isSignUp() {
        _uiState.value = _uiState.value.copy(authPage = AuthPage.signUp)
    }

    fun isSignIn() {
        _uiState.value = _uiState.value.copy(authPage = AuthPage.SignIn)
    }

    fun inputReset() {
        _uiState.value = _uiState.value.copy(authInput = AuthInput())
    }

    fun authStateReset() {
        _uiState.value = _uiState.value.copy(authState = AuthState.Idle)
    }

    fun authPageReset() {
        _uiState.value = _uiState.value.copy(authPage = AuthPage.SignIn)
    }

    fun sessionStateReset() {
        _uiState.value = _uiState.value.copy(sessionState = SessionState.Idle)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FXInsightApplication
                val repository = application.container.fxInsightRepository
                AuthViewModel(repository)
            }
        }
    }
}