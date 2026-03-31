package com.example.fxinsight.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fxinsight.application.FXInsightApplication
import com.example.fxinsight.data.repositiory.FXInsightRepository
import com.example.fxinsight.model.uistate.AuthInput
import com.example.fxinsight.model.uistate.AuthPage
import com.example.fxinsight.model.uistate.AuthState
import com.example.fxinsight.model.uistate.AuthUiState
import com.example.fxinsight.model.uistate.SessionState
import io.github.jan.supabase.auth.Auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repositiory: FXInsightRepository): ViewModel() {

    private val _uistate =  MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uistate



    //[Welcome Screen]
    //---------------------------------------------------------------------------

    //When user click the button
    fun clickWelcomeButton()
    {
        viewModelScope.launch{
            _uistate.value = _uistate.value.copy(
                sessionState = SessionState.Checking
            )
            if (repositiory.isSession())
            {
                _uistate.value = _uistate.value.copy(
                    sessionState = SessionState.Authenticated
                )
            }
            else
            {
                _uistate.value = _uistate.value.copy(
                    sessionState = SessionState.unAutheticated
                )
            }
        }
    }

    // [Auth Screen]
// -----------------------------------------------------------------------------
    // update user input
    fun updateEmail(email: String)
    {
        _uistate.value = _uistate.value.copy(
            authInput = _uistate.value.authInput.copy(
                email = email
            )
        )

    }
    fun updateUserName(userName: String?)
    {
        _uistate.value = _uistate.value.copy(
            authInput = _uistate.value.authInput.copy(
                userName = userName
            )
        )
    }
    fun updatePassword(password: String) {
        _uistate.value = _uistate.value.copy(
            authInput = _uistate.value.authInput.copy(
                password = password
            )
        )
    }
 //  Scenario handling the welcome screen navigation by session
// ---------------------------------------------------------------------------
    // Viewmodel checks username not blank, email not balnk , passwordl length valid
    // if valid call repository


    // When user Clicks SignUp or Login button
    fun SignIn(email: String, password: String )
    {
        viewModelScope.launch{
            _uistate.value = _uistate.value.copy(
                authState = AuthState.Loading
            )

            if (!(verifyInput(email, password, null)))
            {
                _uistate.value = _uistate.value.copy(
                    authState = AuthState.Error(message = "Failed Input")
                )
            }
            val result = repositiory.signIn(email, password)

            result.fold(
                onSuccess = {
                    _uistate.value = _uistate.value.copy(
                        authState = AuthState.Success
                    )
                    Log.d("SignIn", "Succesfully loggedin")
                },
                onFailure = {error ->
                    _uistate.value = _uistate.value.copy(
                        authState = AuthState.Error(message = error.message ?: "Something went wrong with Sign in")
                    )
                    Log.d("SignIn", "Signup unsuccesful")
                },
            )
        }
    }

    // next work on session valid
    // Don't forget reset authstate

    fun SignUp(email: String, password: String, userName: String)
    {
        viewModelScope.launch{
            _uistate.value = _uistate.value.copy(
                authState = AuthState.Loading
            )
            if (!(verifyInput(email, password, null)))
            {
                _uistate.value = _uistate.value.copy(
                    authState = AuthState.Error(message = "Failed Input")
                )
            }

            val result = repositiory.signUp(email = email, password = password, userName = userName)

            result.fold(
                onSuccess = {
                    _uistate.value = _uistate.value.copy(
                        authState = AuthState.Success
                    )
                    Log.d("SignUp", "Signup succesfully")
                },
                onFailure = {error ->
                    _uistate.value = _uistate.value.copy(
                        authState = AuthState.Error(message = error.message?: "Something wrong with SignUp")
                    )
                    Log.d("SignUP", "Signup unsuccesful")
                },
            )
        }

    }


    // Helper
    fun verifyInput(email: String,password: String, userName: String?): Boolean
    {
        var valid: Boolean = true
        if (!isValidEmail(email))
        {
            _uistate.value = _uistate.value.copy(
                authInput = _uistate.value.authInput.copy(
                    emailError = "Please enter a valid email address"
                )
            )
         valid = false
        }
        if (password.length < 8)
        {
            _uistate.value = _uistate.value.copy(
                authInput = _uistate.value.authInput.copy(
                    passwordError = "Please enter a password that is at least 8 characters"
                )
            )
            valid = false
        }
        if (userName.isNullOrBlank() && _uistate.value.authPage == AuthPage.signUp)
        {
            _uistate.value = _uistate.value.copy(

                authInput = _uistate.value.authInput.copy(
                    userNameError = "Please enter a valid user name "
                )
            )
            valid = false
        }

        return valid
    }

    // Check valid Email address
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
//----------------------------------------------------------------------------
    // When navigate to signup
    fun isSignUp()
    {
        _uistate.value = _uistate.value.copy(
            authPage = AuthPage.signUp
        )
    }
    // When navigate to signIn
    fun isSignIn()
    {
        _uistate.value = _uistate.value.copy(
            authPage = AuthPage.SignIn
        )
    }

// ---------------------------------------------------------------------------
    // Reset value
    fun inputReset()
    {
        _uistate.value = _uistate.value.copy(
            authInput = AuthInput()
        )
    }
    fun authStateReset()
    {
        _uistate.value = _uistate.value.copy(
            authState = AuthState.Idle
        )
    }
    fun authPageReset()
    {
        _uistate.value = _uistate.value.copy(
            authPage = AuthPage.SignIn
        )
    }
    fun sessionStateReset()
    {
        _uistate.value = _uistate.value.copy(
            sessionState = SessionState.Idle
        )
    }




    companion object{

        val Factory: ViewModelProvider.Factory = viewModelFactory{

            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY] as FXInsightApplication)
                val repositiory = application.container.fxInsightRepository

                AuthViewModel(repositiory = repositiory)
            }

        }
    }
}