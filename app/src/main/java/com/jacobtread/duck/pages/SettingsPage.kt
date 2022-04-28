package com.jacobtread.duck.pages

import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable

object SettingsPage : Page("Settings", "settings", Icons.Filled.Settings) {
    @Composable
    override fun Content() {
        Text("Settings Page")
    }
}