package com.jacobtread.duck.screens

import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

object HomePage : Page("home", Icons.Filled.Home, "Home") {
    @Composable
    override fun Root(navController: NavHostController, modifier: Modifier) {
        Text("Home Page")
    }
}