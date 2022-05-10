package com.jacobtread.duck.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.jacobtread.duck.flow.RetryFlow
import com.jacobtread.duck.flow.rememberRetryFlowState
import com.jacobtread.duck.socket.DuckSocket
import com.jacobtread.duck.socket.command.commands.FileResponse
import com.jacobtread.duck.socket.command.commands.FilesCommand

object FilesPage : Page("Files", "files", Icons.Filled.Folder) {

    @Composable
    override fun Content(navController: NavHostController, stackEntry: NavBackStackEntry) {
        val files = remember { mutableListOf<FileResponse>() }
        val flowState = rememberRetryFlowState()
        val scrollState = rememberLazyListState()
        RetryFlow(
            load = {
                val loadedFiles = DuckSocket.send(FilesCommand())
                files.addAll(loadedFiles)
            },
            errorTitle = "Failed to load",
            loadingTitle = "Loading Files",
            loadingMessage = "Retrieving files from websocket",
            state = flowState
        ) {
            Column {
                IconButton(
                    modifier = Modifier
                        .padding(15.dp),
                    onClick = {
                        // TODO: Add new entry
                    }
                ) {
                    Icon(Icons.Filled.CreateNewFolder, contentDescription = null)
                }
                Files(files, navController, scrollState)
            }
        }
    }

    @Composable
    fun Files(files: List<FileResponse>, navController: NavHostController, scrollState: LazyListState) {
        LazyColumn(
            Modifier.padding(15.dp),
            scrollState
        ) {
            for (index in files.indices) {
                val file = files[index]
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(
                                    FilePage.route
                                        .replace("/{fileName}", file.name)
                                ) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(15.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(15.dp)
                        ) {
                            Icon(Icons.Filled.Folder, contentDescription = null)
                            Column(
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(file.name, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("${file.size} bytes", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}