package com.jacobtread.duck.screens

import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

object FilesPage : Page("files", Icons.Filled.Folder, "Files") {
    @Composable
    override fun Root(navController: NavHostController, modifier: Modifier) {
        Text("Files Page")
    }
}