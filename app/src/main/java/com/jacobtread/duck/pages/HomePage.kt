package com.jacobtread.duck.pages

import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

object HomePage : Page("Home", "home", Icons.Filled.Home) {
    @Composable
    override fun Content(navController: NavHostController, stackEntry: NavBackStackEntry) {
        Text("Home Page")
    }
}