package com.jacobtread.duck.screens

import android.view.WindowInsets
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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.jacobtread.duck.api.DuckController
import com.jacobtread.duck.api.TerminalConsumer
import com.jacobtread.duck.api.TerminalMessage
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.imePadding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object TerminalPage : Page("terminal", Icons.Filled.Code, "Terminal") {

    @Composable
    override fun Root(navController: NavHostController, modifier: Modifier) {
        val lines = remember { mutableStateListOf<String>() }
        val scrollState = rememberLazyListState()
        val scope = rememberCoroutineScope();
        SideEffect {
            DuckController.terminalConsumer = object : TerminalConsumer {
                fun update() {
                    scope.launch {
                        if (!scrollState.isScrollInProgress) {
                            scrollState.animateScrollToItem(lines.size - 1)
                        }
                    }
                }

                override fun bulk(value: List<String>) {
                    lines.addAll(value)
                    update()
                }

                override fun consume(value: String) {
                    lines.add(value)
                    update()
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
            UserInput(
                Modifier
                    .navigationBarsPadding()
                    .imePadding()
            )
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
    fun UserInput(modifier: Modifier) {

        Row(modifier) {
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
            }) {
                Icon(Icons.Filled.Send, contentDescription = "Send")
            }

        }
    }

}
