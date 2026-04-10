package com.example.fxinsight.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fxinsight.R
import com.example.fxinsight.ui.uistate.AuthPage
import com.example.fxinsight.ui.theme.FXInsightTheme


//Auth state
// login ,
// SIgn up!!!!!!!!!!
@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    email: String,
    password: String,
    username: String,
    updateEmail: (String) -> Unit,
    updatePassword: (String) -> Unit,
    updateUserName: (String) -> Unit,
    isSignUp: () -> Unit,
    signIn: (String, String) -> Unit,
    signUp: (String, String, String) -> Unit,
    currentAuthPage: AuthPage,
    errorMessage: String?,
    usernameError: String?,
    emailError: String?,
    passwordError: String?,
) {
    Sign(
        email = email,
        password = password,
        username = username,
        updateEmail = updateEmail,
        updatePassword = updatePassword,
        updateUserName = updateUserName,
        isSignUp = isSignUp,
        signIn = signIn,
        signUp = signUp,
        currentAuthPage = currentAuthPage,
        errorMessage = errorMessage,
        usernameError = usernameError,
        emailError = emailError,
        passwordError = passwordError
    )
}

@Composable
fun Sign(
    modifier: Modifier = Modifier,
    email: String,
    password: String,
    username: String,
    updateEmail: (String) -> Unit,
    updatePassword: (String) -> Unit,
    updateUserName: (String) -> Unit,
    isSignUp: () -> Unit,
    signIn: (String, String) -> Unit,
    signUp: (String, String, String) -> Unit,
    currentAuthPage: AuthPage,
    errorMessage: String?,
    usernameError: String?,
    emailError: String?,
    passwordError: String?
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "auth image"
            )

            Text("FX Insight")

            Text(
                if (currentAuthPage == AuthPage.signUp) {
                    "Create your FX Insight account"
                } else {
                    "Login to FX Insight Account"
                }
            )
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {

            if (currentAuthPage == AuthPage.signUp) {
                Text("User Name")
                TextField(
                    value = username,
                    onValueChange = updateUserName,
                    placeholder = { Text("Enter your username") },
                    isError = usernameError != null,
                    supportingText ={
                        usernameError?.let{
                            Text(text = it)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
//                        .height(55.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text("Email Address")
            TextField(
                value = email,
                onValueChange = updateEmail,
                placeholder = { Text("you@example.com") },
                isError = emailError != null,
                supportingText ={
                    emailError?.let{
                        Text(text = it)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
//                    .height(55.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Password")
            TextField(
                value = password,
                onValueChange = updatePassword,
                placeholder = { Text("At least 8 characters") },
                isError = passwordError != null,
                supportingText ={
                    passwordError?.let{
                        Text(text = it)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
//                    .height(55.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (currentAuthPage == AuthPage.signUp) {
                        signUp(email, password, username)
                    } else {
                        signIn(email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (currentAuthPage == AuthPage.signUp) {
                        "Create"
                    } else {
                        "Sign In"
                    }
                )
            }

            if (currentAuthPage != AuthPage.signUp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Don't have an account?")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign up",
                        modifier = Modifier.clickable(onClick = isSignUp)
                    )
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}



@Composable
@Preview
fun AuthScreenPreview()
{
    FXInsightTheme {

    }
}