package com.jacobtread.duck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.core.view.WindowCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jacobtread.duck.flow.RetryFlow
import com.jacobtread.duck.pages.*
import com.jacobtread.duck.socket.DuckSocket
import com.jacobtread.duck.theme.DuckCentralTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            DuckCentralTheme {
                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier.navigationBarsPadding()
                        .systemBarsPadding()
                ) {
                    App()
                }
            }
        }
    }


    @Composable
    fun App() {
        RetryFlow(
            load = { DuckSocket.connect() },
            errorTitle = "Failed to connect",
            loadingTitle = "Connecting",
            loadingMessage = "Connecting to websocket...",
            state = DuckSocket.connected,
            manualComplete = true,
        ) {
            Pages()
        }
    }


    @Composable
    fun Pages() {
        val navController = rememberNavController()
        val pages = listOf(HomePage, FilesPage, TerminalPage, SettingsPage)
        Scaffold(
            topBar = { StatusBar() },
            bottomBar = { NavigationBar(navController, pages) }
        ) {
            NavHost(
                navController,
                startDestination = pages[0].route,
                modifier = Modifier.fillMaxSize()
                    .padding(it)
            ) {
                pages.forEach { page ->
                    composable(page.route) { backStackEntry ->
                        Column(
                            modifier = Modifier
                                .padding(15.dp)
                        ) {
                            Text(
                                page.name,
                                fontSize = 5.em,
                                fontWeight = FontWeight.Bold,
                            )
                            page.Content(navController, backStackEntry)
                        }

                    }
                }
                composable(
                    FilePage.route,
                    arguments = listOf(navArgument("fileName") { type = NavType.StringType })
                ) { backStackEntry ->
                    FilePage.Content(navController, backStackEntry)
                }
            }
        }
    }

    @Composable
    fun StatusBar() {
        val state = DuckSocket.lastStatus
        Row(
            modifier = Modifier.fillMaxWidth()
                .background(state.color())
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(state.text())
        }
    }

    @Composable
    fun NavigationBar(navController: NavHostController, pages: List<Page>) {
        BottomNavigation {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            pages.forEach { page ->
                BottomNavigationItem(
                    icon = { Icon(page.icon, contentDescription = null) },
                    selected = currentDestination?.hierarchy?.any { it.route == page.route } == true,
                    onClick = {
                        navController.navigate(page.route) {
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
}