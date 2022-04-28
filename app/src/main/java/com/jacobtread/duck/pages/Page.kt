package com.jacobtread.duck.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Page(val name: String, val route: String, val icon: ImageVector) {

    @Composable
    abstract fun Content()
}