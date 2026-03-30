package com.example.fxinsight

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fxinsight.ui.screen.AuthScreen
import com.example.fxinsight.ui.screen.DashboardScreen
import com.example.fxinsight.ui.screen.HistoryScreen
import com.example.fxinsight.ui.screen.MarketScreen
import com.example.fxinsight.ui.screen.WelcomeScreen

@Composable
fun FxApp(modifier: Modifier = Modifier)
{
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "welcome",
    )
    {
        composable(route = "welcome")
        {
            WelcomeScreen(navToNext = {navController.navigate(route = "auth")})

        }
        composable(route = "auth")
        {
            AuthScreen(navToNext = {navController.navigate(route = "main")})

        }
        composable(route = "main")
        {
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
                navToHistory = {navMainController.navigate("history")})
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
        }
    }
}

@Composable
fun BottomNavbar(
    currentRoute: String?,
    navToHome: () -> Unit,
    navToMarket: () -> Unit,
    navToHistory: () -> Unit,

)
{
    NavigationBar{
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = navToHome,
            icon = {
                Icon(Icons.Default.Home, contentDescription = "home")
            },
            label = {Text("Home")}
        )
        NavigationBarItem(
            selected = currentRoute == "market",
            onClick = navToMarket,
            icon = {
                Icon(Icons.Default.AutoGraph, contentDescription = "Market")
            },
            label = {Text("Market")}
        )
        NavigationBarItem(
            selected = currentRoute == "history",
            onClick = navToHistory,
            icon = {
                Icon(Icons.Default.History, contentDescription = "History")
            },
            label = {Text("History")}
        )

    }

}