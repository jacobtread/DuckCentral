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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jacobtread.duck.api.DuckController
import com.jacobtread.duck.api.ResponseHandler
import com.jacobtread.duck.api.SettingsMessage
import com.jacobtread.duck.screens.FilesPage
import com.jacobtread.duck.screens.HomePage
import com.jacobtread.duck.screens.SettingsPage
import com.jacobtread.duck.screens.TerminalPage
import com.jacobtread.duck.ui.theme.DuckCentralTheme
import com.jacobtread.duck.ui.theme.Title

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
}


@Composable
fun Loader(text: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator()
            Spacer(Modifier.width(4.dp))
            Text(text)
        }
    }
}

enum class State {
    Connecting,
    Settings,
    Done
}


@Preview(showBackground = true)
@Composable
fun App() {
    val navController = rememberNavController()
    var state by remember { mutableStateOf(State.Connecting) }
    LaunchedEffect(true) {
        DuckController.connect(callback = { e ->
            if (e != null) {
                e.printStackTrace()
            } else {
                state = State.Settings
                DuckController.push(SettingsMessage()) {
                    println("Received settings")
                    println(it)
                    state = State.Done
                }
            }
        })
    }
    when (state) {
        State.Connecting -> Loader("Connecting to socket..")
        State.Settings -> Loader("Loading settings..")
        State.Done -> Pages(navController)
    }
}

sealed class PageState(var text: String, var color: Color) {
    object Waiting : PageState("Waiting", Color(0xFFF4D32E))
    object Connected : PageState("Connected", Color(0xFF51C158))
    object Running : PageState("Running Unknown Script", Color(0xFF2EF431))
    object Error : PageState("Connection Problem", Color(0xFFF42E42))
}

@Composable
fun Pages(navController: NavHostController) {
    val pages = listOf(
        HomePage,
        FilesPage,
        TerminalPage,
        SettingsPage
    )
    var pageState by remember { mutableStateOf<PageState>(PageState.Waiting) }
    DuckController.stateConsumer = ResponseHandler {
        val value = it.getOrDefault("Internal connection problem")
        if (value.startsWith("running")) {
            pageState = PageState.Running
            val parts = value.split(' ', limit = 1)
            if (parts.size < 2) {
                pageState.text = "Running Unknown Script"
            } else {
                pageState.text = "Running Script \"${parts[1]}\""
            }
        } else if (value.startsWith("connected")) {
            pageState = PageState.Connected

        } else if (value == "Internal connection problem") {
            pageState = PageState.Error
        }
    }
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(pageState.color)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(pageState.text)
            }
        },
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                pages.forEach { screen ->
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
        },
        content = {
            NavHost(navController, startDestination = HomePage.route, modifier = Modifier.fillMaxSize()
                .padding(it)) {
                pages.forEach { page ->
                    composable(page.route) {
                        Surface {
                            Column(
                                modifier = Modifier
                                    .padding(15.dp)
                            ) {
                                Title(page.name)
                                page.Root(navController, Modifier)
                            }
                        }
                    }
                }
            }
        }
    )
}