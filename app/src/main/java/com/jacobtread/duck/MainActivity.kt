package com.jacobtread.duck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jacobtread.duck.api.DuckController
import com.jacobtread.duck.api.ResponseConsumer
import com.jacobtread.duck.api.SettingsMessage
import com.jacobtread.duck.ui.theme.DuckCentralTheme

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

sealed class Page(val route: String, val icon: ImageVector, val name: String) {
    object Home : Page("home", Icons.Filled.Home, "Home")
    object Files : Page("files", Icons.Filled.Folder, "Files")
    object Terminal : Page("terminal", Icons.Filled.Code, "Terminal")
    object Settings : Page("settings", Icons.Filled.Settings, "Settings")
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
    val context = LocalContext.current
    DuckController.stateConsumer = ResponseConsumer {
//        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
    }
    when (state) {
        State.Connecting -> Loader("Connecting to socket..")
        State.Settings -> Loader("Loading settings..")
        State.Done -> Pages(navController)
    }
}

@Composable
fun Pages(navController: NavHostController) {
    val items = listOf(
        Page.Home,
        Page.Files,
        Page.Terminal,
        Page.Settings
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
        NavHost(navController, startDestination = Page.Home.route) {
            composable(Page.Home.route) { Home(navController) }
            composable(Page.Files.route) { Files(navController) }
            composable(Page.Terminal.route) { Terminal(navController) }
            composable(Page.Settings.route) { Settings(navController) }
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