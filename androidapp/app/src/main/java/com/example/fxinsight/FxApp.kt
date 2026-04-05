package com.example.fxinsight

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fxinsight.model.uistate.AuthPage
import com.example.fxinsight.model.uistate.AuthState
import com.example.fxinsight.model.uistate.SessionState
import com.example.fxinsight.ui.screen.AuthScreen
import com.example.fxinsight.ui.screen.DashboardScreen
import com.example.fxinsight.ui.screen.HistoryScreen
import com.example.fxinsight.ui.screen.MarketScreen
import com.example.fxinsight.ui.screen.ProfileScreen
import com.example.fxinsight.ui.screen.WelcomeScreen
import com.example.fxinsight.ui.viewmodel.AuthViewModel

@Composable
fun FxApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "welcome",
        modifier = modifier
    ) {
        composable(route = "welcome") {
            WelcomeScreen(
                clickWelcomeButton = viewModel::clickWelcomeButton
            )

            LaunchedEffect(uiState.sessionState) {
                Log.d("welcome Screen", "${uiState.sessionState}")
                Log.d("state check", "${uiState}")
                when (uiState.sessionState) {
                    is SessionState.Authenticated -> {
                        navController.navigate("main")
                        viewModel.sessionStateReset()
                    }


                    is SessionState.unAutheticated -> {
                        navController.navigate("auth")
                        viewModel.sessionStateReset()
                    }

                    else -> Unit
                }
            }
        }

        composable(route = "auth") {
            val errorMessage = (uiState.authState as? AuthState.Error)?.message

            LaunchedEffect(uiState.authState) {
                if (uiState.authState == AuthState.Success) {
                    navController.navigate("main") {
                        popUpTo("auth") { inclusive = true }
                    }
                    viewModel.authStateReset()
                }
            }

            Log.d("navigation", "The authPage is ${uiState.authPage}")

            AuthScreen(
                email = uiState.authInput.email,
                password = uiState.authInput.password,
                username = uiState.authInput.userName ?: "",
                updateEmail = viewModel::updateEmail,
                updatePassword = viewModel::updatePassword,
                updateUserName = viewModel::updateUserName,
                isSignUp = viewModel::isSignUp,
                signIn = { email, password ->
                    Log.d("AUTH_DEBUG", "Running signIn")
                    viewModel.signIn(email, password)
                },
                signUp = { email, password, userName ->
                    Log.d("AUTH_DEBUG", "Running signUp")
                    viewModel.signUp(email, password, userName)
                },
                currentAuthPage = uiState.authPage,
                errorMessage = errorMessage
            )
        }

        composable(route = "main") {
            MainScaffold()
        }
    }
}

@Composable
fun MainScaffold()
{
    val navMainController = rememberNavController()
    val navBackStackEntry by navMainController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        bottomBar = {
            BottomNavbar(currentRoute = currentRoute,
                navToHome = {navMainController.navigate("home")},
                navToMarket = {navMainController.navigate("market")},
                navToHistory = {navMainController.navigate("history")},
                navToProfile = {navMainController.navigate(("profile"))})
        }

    )
    { innerPadding ->
        NavHost(
            navController = navMainController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        )
        {
            composable(route = "home")
            {
                DashboardScreen()

            }
            composable("market")
            {
                MarketScreen()

            }
            composable("history")
            {
                HistoryScreen()

            }
            composable("profile")
            {
                ProfileScreen()
            }
        }
    }
}

@Composable
fun BottomNavbar(
    currentRoute: String?,
    navToHome: () -> Unit,
    navToMarket: () -> Unit,
    navToHistory: () -> Unit,
    navToProfile: () -> Unit,

)
{
    NavigationBar{

        NavigationBarItem(
            selected = currentRoute == "market",
            onClick = navToMarket,
            icon = {
                Icon(Icons.Default.AutoGraph, contentDescription = "Market")
            },
            label = {Text("Market")}
        )
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = navToHome,
            icon = {
                Icon(Icons.Default.Home, contentDescription = "home")
            },
            label = {Text("Home")}
        )
        NavigationBarItem(
            selected = currentRoute == "history",
            onClick = navToHistory,
            icon = {
                Icon(Icons.Default.History, contentDescription = "History")
            },
            label = {Text("History")}
        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = navToProfile,
            icon = {
                Icon(Icons.Default.Person, contentDescription = "History")
            },
            label = {Text("Profile")}
        )
    }
}