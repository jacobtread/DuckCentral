package com.jacobtread.duck.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.jacobtread.duck.api.DuckController
import com.jacobtread.duck.api.TerminalConsumer
import com.jacobtread.duck.api.TerminalMessage

object TerminalPage : Page("terminal", Icons.Filled.Code, "Terminal") {

    @Composable
    fun UserInput() {
        Row {
            var message = ""
            TextField(
                message,
                onValueChange = {
                    message = it
                }
            )
            IconButton(onClick = {
                if (message.isNotBlank()) {
                    DuckController.push(TerminalMessage(message)) {}
                }
            }) {
                Icon(Icons.Filled.Send, contentDescription = "Send")
            }

        }
    }

    @Composable
    fun Messages(
        lines: List<String>,
        scrollState: LazyListState,
        modifier: Modifier = Modifier,
    ) {
        LazyColumn(
            state = scrollState,
            modifier = modifier
        ) {
            for (index in lines.indices) {
                item {
                    val line = lines[index]
                    Text(line)
                }
            }
        }
    }

    @Composable
    override fun Root(navController: NavHostController, modifier: Modifier) {
        val lines = remember { mutableStateListOf<String>() }
        val scrollState = rememberLazyListState()
        SideEffect {
            DuckController.terminalConsumer = object : TerminalConsumer {
                override fun bulk(value: List<String>) {
                    lines.addAll(value)
                }

                override fun consume(value: String) {
                    lines.add(value)
                }
            }
        }
        Column(modifier) {
            Messages(
                lines,
                scrollState,
                Modifier.weight(1f)
                    .fillMaxSize()
            )
            UserInput()
        }
    }
}
