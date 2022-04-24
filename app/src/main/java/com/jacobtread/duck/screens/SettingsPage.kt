package com.jacobtread.duck.screens

import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

object SettingsPage : Page("settings", Icons.Filled.Settings, "Settings") {
    @Composable
    override fun Root(navController: NavHostController, modifier: Modifier) {
        Text("Settings Page")
    }
}