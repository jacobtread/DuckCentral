package com.jacobtread.duck.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController

sealed class Page(val route: String, val icon: ImageVector, val name: String) {
    @Composable
    abstract fun Root(navController: NavHostController, modifier: Modifier)
}