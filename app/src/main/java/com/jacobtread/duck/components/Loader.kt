package com.jacobtread.duck.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
            Text(content,fontSize = 16.sp, color = Color.LightGray)
        }
    }
}

@Composable
fun RetryLayout(title: String, message: String, onRetry: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(DefaultSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(title, fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Text(
                message,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.LightGray,
                softWrap = true,
                modifier = Modifier.padding(horizontal = 25.dp),
            )
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}