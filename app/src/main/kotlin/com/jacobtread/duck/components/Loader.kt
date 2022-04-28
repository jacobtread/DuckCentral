package com.jacobtread.duck.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.jacobtread.duck.theme.DefaultSpacing

/**
 * Loader Composable function for displaying loading screens
 * as they are fairly common
 *
 * @param title The message to display as the title
 * @param content The content message to display
 */
@Composable
fun Loader(title: String, content: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(DefaultSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator()
            Text(title, fontSize = 18.sp, color = Color.White)
            Text(content)
        }
    }
}