package com.jacobtread.duck.pages

import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable

object HomePage : Page("Home", "home", Icons.Filled.Home) {
    @Composable
    override fun Content() {
        Text("Home Page")
    }
}