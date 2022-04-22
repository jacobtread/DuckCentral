package com.jacobtread.duck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jacobtread.duck.ui.theme.DuckCentralTheme

const val HOST_ADDR = "192.168.4.1"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DuckCentralTheme {
                Surface(color = MaterialTheme.colors.background) {
                    App()
                }
            }
        }
    }
}

sealed class Screen(val route: String, val icon: ImageVector, val name: String) {
    object Home : Screen("home", Icons.Filled.Home, "Home")
    object Files : Screen("files", Icons.Filled.Folder, "Files")
    object Terminal : Screen("terminal", Icons.Filled.Code, "Terminal")
    object Settings : Screen("settings", Icons.Filled.Settings, "Settings")
}

@Preview(showBackground = true)
@Composable
fun App() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Home,
        Screen.Files,
        Screen.Terminal,
        Screen.Settings
    )
    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) {
        NavHost(navController, startDestination = Screen.Home.route) {
            composable(Screen.Home.route) { Home(navController) }
            composable(Screen.Files.route) { Files(navController) }
            composable(Screen.Terminal.route) { Terminal(navController) }
            composable(Screen.Settings.route) { Settings(navController) }
        }
    }
}

@Composable
fun Home(navController: NavHostController) {
    Text("Home Page")
}

@Composable
fun Files(navController: NavHostController) {
    Text("Files Page")
}

@Composable
fun Terminal(navController: NavHostController) {
    Text("Terminal Page")
}

@Composable
fun Settings(navController: NavHostController) {
    Text("Settings Page")

}