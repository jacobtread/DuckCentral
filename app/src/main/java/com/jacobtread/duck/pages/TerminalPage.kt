package com.jacobtread.duck.pages

import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.runtime.Composable

object TerminalPage : Page("Terminal", "terminal", Icons.Filled.Terminal) {
    @Composable
    override fun Content() {
        Text("Terminal Page")
    }
}