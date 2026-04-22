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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fxinsight.ui.uistate.AuthState
import com.example.fxinsight.ui.uistate.SessionState
import com.example.fxinsight.ui.screen.AlertScreen
import com.example.fxinsight.ui.screen.AuthScreen
import com.example.fxinsight.ui.screen.DashboardScreen
import com.example.fxinsight.ui.screen.HistoryScreen
import com.example.fxinsight.ui.screen.MarketScreen
import com.example.fxinsight.ui.screen.ProfileScreen
import com.example.fxinsight.ui.screen.WelcomeScreen
import com.example.fxinsight.ui.uistate.FavUiState
import com.example.fxinsight.ui.viewmodel.AlertViewModel
import com.example.fxinsight.ui.viewmodel.AuthViewModel
import com.example.fxinsight.ui.viewmodel.FavoriteViewModel
import com.example.fxinsight.ui.viewmodel.HistoryViewModel
import com.example.fxinsight.ui.viewmodel.HomeViewModel
import com.example.fxinsight.ui.viewmodel.MarketViewModel

@Composable
fun FxApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
    val uiState by authViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "welcome",
        modifier = modifier
    ) {
        composable(route = "welcome") {
            WelcomeScreen(
                clickWelcomeButton = authViewModel::clickWelcomeButton
            )
            LaunchedEffect(uiState.sessionState) {
                Log.d("welcome Screen", "${uiState.sessionState}")
                Log.d("state check", "${uiState}")
                when (uiState.sessionState) {
                    is SessionState.Authenticated -> {
                        Log.d("welcome Screen authenticated Confirmed", "${uiState.sessionState}")
                        navController.navigate("main")
                    }
                    is SessionState.unAutheticated -> {
                        navController.navigate("auth")
                        authViewModel.sessionStateReset()
                    }

                    else -> Unit
                }
            }
        }

        composable(route = "auth") {
            val errorMessage = (uiState.authState as? AuthState.Error)?.message
            Log.d("auth route", "${uiState.sessionState}")

            LaunchedEffect(uiState.authState) {
                if (uiState.authState == AuthState.Success) {
                    navController.navigate("main") {
                        popUpTo("auth") { inclusive = true }
                    }
                    authViewModel.authStateReset()
                    authViewModel.inputReset()
                }
            }
            LaunchedEffect(uiState.authPage) {
                authViewModel.inputReset()
                authViewModel.authStateReset()
                Log.d("auth", "triggered")
            }

            AuthScreen(
                email = uiState.authInput.email,
                password = uiState.authInput.password,
                username = uiState.authInput.userName ?: "",
                updateEmail = authViewModel::updateEmail,
                updatePassword = authViewModel::updatePassword,
                updateUserName = authViewModel::updateUserName,
                isSignUp = authViewModel::isSignUp,
                isSignIn = authViewModel::isSignIn,
                signIn = { email, password ->
                    Log.d("AUTH_DEBUG", "Running signIn")
                    authViewModel.signIn(email, password)
                },
                signUp = { email, password, userName ->
                    Log.d("AUTH_DEBUG", "Running signUp")
                    authViewModel.signUp(email, password, userName)
                },
                currentAuthPage = uiState.authPage,
                errorMessage = errorMessage,
                usernameError = uiState.authInput.userNameError,
                emailError = uiState.authInput.emailError,
                passwordError = uiState.authInput.passwordError

            )
        }

        composable(route = "main") {
            MainScaffold(authViewModel::signOut,
                currentSession = uiState.sessionState,
                navBackWelcome = {navController.navigate("welcome")
                {
                    popUpTo("main") { inclusive = true }
                }
                    authViewModel.sessionStateReset()
                }
            )
        }
    }
}

@Composable
fun MainScaffold(
    signOut: () -> Unit,
    currentSession: SessionState,
    navBackWelcome: () -> Unit ,

)
{
    val snackbarHostState = remember { SnackbarHostState() }

    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
    val favViewModel: FavoriteViewModel = viewModel(factory = FavoriteViewModel.Factory)
    val marketViewModel: MarketViewModel = viewModel(factory = MarketViewModel.Factory)
    val alertViewModel: AlertViewModel = viewModel(factory = AlertViewModel.Factory)
    val historyViewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.Factory)
    
    val navMainController = rememberNavController()
    val navBackStackEntry by navMainController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

        bottomBar = {
            BottomNavbar(
                currentRoute = currentRoute,
                navToHome = {
                    navMainController.navigate("home") {
                        popUpTo(navMainController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                navToMarket = {
                    navMainController.navigate("market") {
                        popUpTo(navMainController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                navToHistory = {
                    navMainController.navigate("history") {
                        popUpTo(navMainController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                navToProfile = {
                    navMainController.navigate("profile") {
                        popUpTo(navMainController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
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

                DashboardScreen(
                    homeViewModel = homeViewModel, 
                    favViewModel = favViewModel,
                    onNavToAlerts = { navMainController.navigate("alerts") }
                )
                val favoriteUiState by favViewModel.uiState.collectAsState()

                LaunchedEffect(favoriteUiState.favCreate) {
                    if (favoriteUiState.favCreate is FavUiState.Error) {
                        val message = (favoriteUiState.favCreate as FavUiState.Error).message

                        snackbarHostState.showSnackbar(
                            message ?: "Failed to add favorite"
                        )

                        favViewModel.resetFavCreate()
                    }
                }

            }
            composable("market")
            {
                MarketScreen(homeViewModel = homeViewModel, marketViewModel = marketViewModel)

            }
            composable("history")
            {
                HistoryScreen(viewModel = historyViewModel)

            }
            composable("profile")
            {
                LaunchedEffect(currentSession) {
                    Log.d("MainScaffold", "${currentSession}")
                    if (currentSession is SessionState.unAutheticated)
                    {
                        navBackWelcome()

                    }
                }
                ProfileScreen(signOut = signOut)
            }
            composable("alerts") {
                AlertScreen(
                    viewModel = alertViewModel,
                    homeViewModel = homeViewModel,
                    onBack = { navMainController.popBackStack() }
                )
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