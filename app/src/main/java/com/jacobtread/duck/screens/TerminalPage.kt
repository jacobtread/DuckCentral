package com.jacobtread.duck.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jacobtread.duck.api.DuckController
import com.jacobtread.duck.api.TerminalMessage
import com.jacobtread.duck.api.TerminalState


object TerminalPage : Page("terminal", Icons.Filled.Code, "Terminal") {

    @Composable
    override fun Root(navController: NavHostController, modifier: Modifier) {
        val scrollState = rememberLazyListState()
        val terminalState = DuckController.terminalState(scrollState)
        Column(modifier) {
            Messages(
                terminalState,
                scrollState,
                Modifier.weight(1f)
                    .fillMaxSize()
                    .padding(10.dp)
            )
            UserInput(
                Modifier
                    .navigationBarsPadding()
                    .imePadding()
            )
        }
    }

    @Composable
    fun Messages(
        state: TerminalState,
        scrollState: LazyListState,
        modifier: Modifier = Modifier,
    ) {

        LazyColumn(
            state = scrollState,
            modifier = modifier
        ) {
            for (index in state.lines.indices) {
                item {
                    val line = state.lines[index]
                    Text(line, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }


    @Composable
    fun UserInput(modifier: Modifier) {
        Surface(
            elevation = 3.dp,
            modifier = modifier
        ) {
            Row {
                val (message, setMessage) = remember { mutableStateOf("") }

                TextField(
                    value = message,
                    onValueChange = setMessage,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    if (message.isNotBlank()) {
                        DuckController.push(TerminalMessage(message)) {}
                    }
                    setMessage("")
                }) {
                    Icon(Icons.Filled.Send, contentDescription = "Send")
                }

            }
        }
    }

}
