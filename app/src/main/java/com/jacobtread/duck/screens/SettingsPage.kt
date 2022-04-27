package com.jacobtread.duck.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.jacobtread.duck.api.DuckController
import com.jacobtread.duck.api.Settings
import com.jacobtread.duck.api.SettingsMessage

class SettingsState {
    var ssid by mutableStateOf("")
    var password by mutableStateOf("")
    var channel by mutableStateOf("")
    var autorun by mutableStateOf("")

    fun setFrom(settings: Settings) {
        ssid = settings.ssid
        password = settings.password
        channel = settings.channel
        autorun = settings.autorun
    }
}

object SettingsPage : Page("settings", Icons.Filled.Settings, "Settings") {
    @Composable
    override fun Root(navController: NavHostController, modifier: Modifier) {
        val settings = remember { SettingsState() }
        LaunchedEffect(true) {
            val value = DuckController.waitFor(SettingsMessage())
            println(value)
            settings.setFrom(value)
        }
        Column {
            Text("Settings Page")
            Inputs(settings)
        }
    }

    @Composable
    fun Inputs(state: SettingsState) {
        Column {
            TextField(state.ssid, onValueChange = { state.ssid = it }, label = {Text("SSID")})
            TextField(state.password, onValueChange = { state.password = it }, label = {Text("Password")})
            TextField(state.channel, onValueChange = { state.channel = it }, label = {Text("Channel")})
            TextField(state.autorun, onValueChange = { state.autorun = it }, label = {Text("AutoRun")})
        }
    }
}