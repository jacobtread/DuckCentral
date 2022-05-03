package com.jacobtread.duck.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

sealed class Page(val name: String, val route: String, val icon: ImageVector) {

    @Composable
    abstract fun Content(navController: NavHostController, stackEntry: NavBackStackEntry)
}