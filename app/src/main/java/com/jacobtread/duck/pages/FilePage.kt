package com.jacobtread.duck.pages

import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.runtime.*
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.jacobtread.duck.flow.RetryFlow
import com.jacobtread.duck.socket.DuckController
import com.jacobtread.duck.socket.command.commands.ReadFileCommand

object FilePage : Page("Editing File", "file/{fileName}", Icons.Filled.Folder) {

    @Composable
    override fun Content(navController: NavHostController, stackEntry: NavBackStackEntry) {
        val fileName = stackEntry.arguments!!.getString("fileName")!!
        Text("Editing $fileName")
        var fileContent by remember { mutableStateOf("") }
        RetryFlow(
            load = {
                val retrieved = DuckController.send(ReadFileCommand(fileName))
                fileContent = retrieved
            },
            errorTitle = "Failed to load",
            loadingTitle = "Loading File",
            loadingMessage = "Retrieving file from websocket"
        ) {
            Text(fileContent)
        }
    }
}