package com.jacobtread.duck.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavHostController
import com.jacobtread.duck.Loader
import com.jacobtread.duck.api.*

class SettingsState {
    var set by mutableStateOf(false)
    var ssid by mutableStateOf("")
    var password by mutableStateOf("")
    var channel by mutableStateOf("")
    var autorun by mutableStateOf("")
    val files = mutableListOf<File>()

    fun setFrom(settings: Settings, files: List<File>) {
        ssid = settings.ssid
        password = settings.password
        channel = settings.channel
        autorun = settings.autorun
        this.files.addAll(files)
        set = true
    }
}

suspend fun tryLoadSettings(state: SettingsState) {
    val value = DuckController.waitFor(SettingsMessage())
    val files = DuckController.waitFor(FilesMessage())
    state.setFrom(value, files)
}

object SettingsPage : Page("settings", Icons.Filled.Settings, "Settings") {
    @Composable
    override fun Root(navController: NavHostController, modifier: Modifier) {
        var error by remember { mutableStateOf<String?>(null) }
        val settings = remember { SettingsState() }
        LaunchedEffect(true) {
            try {
                tryLoadSettings(settings)
            } catch (e: Throwable) {
                error = e.message ?: "Unknown Error"
            }
        }

        val err = error
        if (err != null) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Failed to load settings:")
                Text(err)
            }
        } else {
            if (!settings.set) {
                Loader("Loading settings...")
            } else {
                Inputs(settings)
            }
        }
    }

    @Composable
    fun Inputs(state: SettingsState) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(top = 20.dp)
        ) {
            TextField(state.ssid, onValueChange = { state.ssid = it }, label = { Text("SSID") }, modifier = Modifier.fillMaxWidth())
            TextField(state.password, onValueChange = { state.password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())
            TextField(state.channel, onValueChange = { state.channel = it }, label = { Text("Channel") }, modifier = Modifier.fillMaxWidth())
            AutoRunInput(state)
            Button(onClick = {

            }, modifier = Modifier.fillMaxWidth()) {
                Text("Save Settings")
            }
        }
    }

    @Composable
    fun AutoRunInput(state: SettingsState) {
        Column {
            var expanded by remember { mutableStateOf(false) }
            val icon = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
            var value by remember { mutableStateOf(state.autorun) }
            var textFieldSize by remember { mutableStateOf(Size.Zero) }
            TextField(
                value,
                onValueChange = {},
                readOnly = true,
                leadingIcon = {
                    Icon(Icons.Filled.AttachFile, null)
                },
                trailingIcon = {
                    IconButton(onClick = {
                        expanded = true
                    }) { Icon(icon, null) }
                },
                modifier = Modifier.fillMaxWidth()
                    .onGloballyPositioned { coords ->
                        textFieldSize = coords.size.toSize()
                    }
            )
            DropdownMenu(
                expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })
            ) {
                DropdownMenuItem(onClick = {
                    value = "None"
                    expanded = false
                }) {
                    Row {
                        Text("None")
                    }
                }
                state.files.forEach { file ->
                    DropdownMenuItem(onClick = {
                        value = file.name
                        expanded = false
                    }) {
                        Text(file.name)
                    }
                }
            }
        }
    }

}
