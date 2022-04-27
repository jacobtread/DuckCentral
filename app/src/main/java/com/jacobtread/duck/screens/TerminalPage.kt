package com.jacobtread.duck.screens

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.jacobtread.duck.api.DuckController
import com.jacobtread.duck.api.TerminalMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class TerminalState(private val scrollState: LazyListState, private val scope: CoroutineScope) {
    val lines = mutableStateListOf<String>()

    private fun update() {
        scope.launch {
            if (!scrollState.isScrollInProgress) {
                scrollState.animateScrollToItem(lines.size - 1)
            }
        }
    }


    fun bulk(value: List<String>) {
        lines.addAll(value)
        update()
    }

    fun line(value: String) {
        lines.add(value)
        update()
    }
}


object TerminalPage : Page("terminal", Icons.Filled.Code, "Terminal") {

    @Composable
    override fun Root(navController: NavHostController, modifier: Modifier) {
        val scope = rememberCoroutineScope();
        val scrollState = rememberLazyListState()
        val terminalState = remember { TerminalState(scrollState, scope) }
        DisposableEffect(LocalLifecycleOwner.current) {
            DuckController.terminalState = terminalState;
            onDispose {
                DuckController.terminalState = null;
            }
        }

        Column(modifier) {
            Messages(
                terminalState,
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
