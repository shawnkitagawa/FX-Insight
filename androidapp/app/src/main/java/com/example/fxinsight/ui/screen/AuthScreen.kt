package com.example.fxinsight.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fxinsight.ui.uistate.AuthPage
import com.example.fxinsight.ui.theme.FXInsightTheme

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
    isSignIn: () -> Unit,
    signIn: (String, String) -> Unit,
    signUp: (String, String, String) -> Unit,
    currentAuthPage: AuthPage,
    errorMessage: String?,
    usernameError: String?,
    emailError: String?,
    passwordError: String?,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0B0B1E), Color(0xFF1A1A3A), Color(0xFF2D1B4D))
                )
            )
    ) {
        // Background decorative glow
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-50).dp)
                .size(400.dp)
                .blur(100.dp)
                .background(Color(0xFF4A90E2).copy(alpha = 0.15f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Logo
            Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = null,
                tint = Color(0xFF00FFC2),
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "FX Insight",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraLight,
                    letterSpacing = 4.sp,
                    color = Color.White
                )
            )

            Text(
                text = if (currentAuthPage == AuthPage.signUp) "Create your account" else "Welcome back",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.5f),
                    letterSpacing = 1.sp
                )
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Input Fields
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (currentAuthPage == AuthPage.signUp) {
                    ModernTextField(
                        value = username,
                        onValueChange = updateUserName,
                        label = "Username",
                        placeholder = "Enter your username",
                        error = usernameError
                    )
                }

                ModernTextField(
                    value = email,
                    onValueChange = updateEmail,
                    label = "Email Address",
                    placeholder = "you@example.com",
                    error = emailError
                )

                ModernTextField(
                    value = password,
                    onValueChange = updatePassword,
                    label = "Password",
                    placeholder = "At least 8 characters",
                    error = passwordError,
                    isPassword = true
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (currentAuthPage == AuthPage.signUp) {
                        signUp(email, password, username)
                    } else {
                        signIn(email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.1f),
                    contentColor = Color.White
                )
            ) {
                Text(
                    if (currentAuthPage == AuthPage.signUp) "Create Account" else "Sign In",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Light)
                )
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentAuthPage == AuthPage.signUp) {
                    Text(
                        text = "Already have an account?",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    )
                    Text(
                        text = "Sign in",
                        modifier = Modifier
                            .clickable(onClick = isSignIn)
                            .padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF00FFC2),
                            fontWeight = FontWeight.Bold
                        )
                    )
                } else {
                    Text(
                        text = "Don't have an account?",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    )
                    Text(
                        text = "Sign up",
                        modifier = Modifier
                            .clickable(onClick = isSignUp)
                            .padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF00FFC2),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    error: String?,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.2f)) },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            isError = error != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00FFC2).copy(alpha = 0.5f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                errorBorderColor = MaterialTheme.colorScheme.error,
                cursorColor = Color(0xFF00FFC2),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            supportingText = {
                error?.let {
                    Text(text = it, style = MaterialTheme.typography.bodySmall)
                }
            }
        )
    }
}

@Composable
@Preview
fun AuthScreenPreview() {
    FXInsightTheme {
//        AuthScreen(
//            email = "", password = "", username = "",
//            updateEmail = {}, updatePassword = {}, updateUserName = {},
//            isSignUp = {}, signIn = {_,_ ->}, signUp = {_,_,_ ->},
//            currentAuthPage = AuthPage.SignIn,
//            errorMessage = null, usernameError = null, emailError = null, passwordError = null
//        )
    }
}
