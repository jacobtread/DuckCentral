package com.jacobtread.duck.pages

import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import com.jacobtread.duck.components.Loader
import com.jacobtread.duck.components.RetryLayout
import com.jacobtread.duck.socket.command.commands.FileResponse

object SettingsPage : Page("Settings", "settings", Icons.Filled.Settings) {

    class SettingsState {
        var load by mutableStateOf(false)
        var failed by mutableStateOf(false)

        var ssid by mutableStateOf("")
        var channel by mutableStateOf("")
        var password by mutableStateOf("")
        var autorun by mutableStateOf("")
        val files = mutableListOf<FileResponse>()
    }

    @Composable
    override fun Content() {
        Text("Settings Page")

        val state = remember { SettingsState() }

        LaunchedEffect(state.load) {
            if (state.load) {
                try {

                } catch (e: Throwable) {
                    state.failed = true
                }
                state.load = false
            }
        }

        if (state.load) {
            Loader("Loading Settings", "Retrieving settings from websocket")
        } else if(state.failed){
            RetryLayout("Failed to load", "Unable to load settings") {

            }
        }

    }

}