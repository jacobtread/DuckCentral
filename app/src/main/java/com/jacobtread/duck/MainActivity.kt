package com.jacobtread.duck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jacobtread.duck.components.Loader
import com.jacobtread.duck.pages.*
import com.jacobtread.duck.socket.DuckController
import com.jacobtread.duck.theme.DefaultSpacing
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

    class BaseState {
        var connect by mutableStateOf(true)
        var failed by mutableStateOf(false)
        var failMessage by mutableStateOf("")
    }


    @Composable
    fun App() {

        val state = remember { BaseState() }

        LaunchedEffect(key1 = state.connect) {
            if (state.connect) {
                try {
                    DuckController.connect()
                } catch (e: Throwable) {
                    state.failed = true
                    state.failMessage = e.message ?: "Unknown cause"
                }
                state.connect = false
            }
        }

        if (DuckController.connected) {
            Pages()
        } else if (state.failed) {
            FailedConnection(state)
        } else {
            Loader("Connecting", "Connecting to websocket...")
        }
    }

    @Composable
    fun FailedConnection(state: BaseState) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(DefaultSpacing),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Failed to connect", fontSize = 28.sp, color = Color.White, fontWeight = FontWeight.Bold)
                Text(
                    state.failMessage,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color.LightGray,
                    softWrap = true,
                    modifier = Modifier.padding(horizontal = 25.dp),
                )
                Button(onClick = {
                    state.failed = false
                    state.connect = true
                }) {
                    Text("Retry")
                }
            }
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
                    composable(page.route) {
                        Column(
                            modifier = Modifier
                                .padding(15.dp)
                        )  {
                            Text(
                                page.name,
                                fontSize = 5.em,
                                fontWeight = FontWeight.Bold,
                            )
                            page.Content()
                        }

                    }
                }
            }
        }
    }

    @Composable
    fun StatusBar() {
        val state = DuckController.lastStatus
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