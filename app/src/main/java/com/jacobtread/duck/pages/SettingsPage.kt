package com.jacobtread.duck.pages

import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.jacobtread.duck.flow.RetryFlow
import com.jacobtread.duck.socket.DuckSocket
import com.jacobtread.duck.socket.command.commands.FileResponse
import com.jacobtread.duck.socket.command.commands.FilesCommand
import com.jacobtread.duck.socket.command.commands.SettingsCommand
import com.jacobtread.duck.socket.command.commands.SettingsResponse

object SettingsPage : Page("Settings", "settings", Icons.Filled.Settings) {

    class SettingsState {
        private var original: SettingsResponse? = null

        var ssid by mutableStateOf("")
        var channel by mutableStateOf("")
        var password by mutableStateOf("")
        var autorun by mutableStateOf("")

        val files = mutableListOf<FileResponse>()

        fun load(settings: SettingsResponse, files: List<FileResponse>) {
            original = settings
            ssid = settings.ssid
            password = settings.password
            channel = settings.channel
            autorun = settings.autorun
            this.files.addAll(files)
        }
    }

    @Composable
    override fun Content(navController: NavHostController, stackEntry: NavBackStackEntry) {
        val state = remember { SettingsState() }
        RetryFlow(
            load = {
                val settings = DuckSocket.send(SettingsCommand())
                val files = DuckSocket.send(FilesCommand())
                state.load(settings, files)
            },
            errorTitle = "Failed to load",
            loadingTitle = "Loading Settings",
            loadingMessage = "Retrieving settings from websocket"
        ) {
            Text("Settings Page")
            Text(state.ssid)
            Text(state.password)
        }
    }

}