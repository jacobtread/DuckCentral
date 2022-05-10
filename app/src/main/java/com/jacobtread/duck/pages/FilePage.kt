package com.jacobtread.duck.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.jacobtread.duck.flow.RetryFlow
import com.jacobtread.duck.socket.DuckSocket
import com.jacobtread.duck.socket.command.commands.ReadFileCommand

object FilePage : Page("Editing File", "file/{fileName}", Icons.Filled.Folder) {

    @Composable
    override fun Content(navController: NavHostController, stackEntry: NavBackStackEntry) {
        val fileName = stackEntry.arguments!!.getString("fileName")!!
        var fileContent by remember { mutableStateOf("") }
        RetryFlow(
            load = {
                val retrieved = DuckSocket.send(ReadFileCommand(fileName))
                fileContent = retrieved
            },
            errorTitle = "Failed to load",
            loadingTitle = "Loading File",
            loadingMessage = "Retrieving file from websocket"
        ) {
            Column {
                Text("Editing $fileName")
                TextField(fileContent, onValueChange = { fileContent = it }, modifier = Modifier.weight(1f))
            }
        }
    }
}