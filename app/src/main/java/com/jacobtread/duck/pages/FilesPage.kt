package com.jacobtread.duck.pages

import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.runtime.Composable

object FilesPage : Page("Files", "files", Icons.Filled.Folder) {
    @Composable
    override fun Content() {
        Text("Files Page")
    }
}