package com.jacobtread.duck.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jacobtread.duck.api.DuckController
import com.jacobtread.duck.api.SimpleMessage
import com.jacobtread.duck.state.TerminalState
import com.jacobtread.duck.state.TerminalState.LineType
import com.jacobtread.duck.ui.theme.ErrorColor
import com.jacobtread.duck.ui.theme.ReceivedColor
import com.jacobtread.duck.ui.theme.SentColor
import kotlinx.coroutines.launch


object TerminalPage : Page("terminal", Icons.Filled.Terminal, "Terminal") {

    @Composable
    override fun Root(navController: NavHostController, modifier: Modifier) {
        val scrollState = rememberLazyListState()
        val terminalState = remember { TerminalState }
        Column(modifier) {
            Messages(
                terminalState,
                scrollState,
                Modifier.weight(1f)
                    .padding(10.dp)
            )
            UserInput(
                terminalState,
                scrollState,
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
                    when (line.type) {
                        LineType.Received -> Text(line.content, fontFamily = FontFamily.Monospace, color = ReceivedColor)
                        LineType.Sent -> Text("$ ${line.content}", fontFamily = FontFamily.Monospace, color = SentColor)
                        LineType.Error -> Text(line.content, fontFamily = FontFamily.Monospace, color = ErrorColor)

                    }
                }
            }
        }
    }


    @Composable
    fun UserInput(
        state: TerminalState,
        scrollState: LazyListState,
    ) {
        var sending by remember { mutableStateOf(false) }
        val (message, setMessage) = remember { mutableStateOf("") }
        val scope = rememberCoroutineScope()
        TextField(
            value = message,
            onValueChange = setMessage,
            readOnly = sending,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        enabled = !sending,
                        onClick = {
                            if (message.isNotBlank()) {
                                sending = true
                                state.addLine(LineType.Sent, message)
                                DuckController.push(SimpleMessage(message)) { result ->
                                    result.onSuccess { line ->
                                        if (line.startsWith("ERROR:")) {
                                            state.addLine(LineType.Error, line)
                                        } else {
                                            state.addLine(LineType.Received, line)
                                        }
                                    }
                                    result.onFailure { ex -> state.addLine(LineType.Error, "An error occurred: ${ex.message ?: "Unknown Error"}") }
                                    scope.launch {
                                        if (!scrollState.isScrollInProgress) {
                                            scrollState.animateScrollToItem(state.end)
                                        }
                                    }
                                    sending = false
                                    setMessage("")
                                }
                            }
                        },
                    ) {
                        Icon(Icons.Filled.Send, contentDescription = "Send")
                    }
                    IconButton(
                        onClick = {
                            state.lines.clear()
                        },
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Clear")
                    }
                }
            }
        )
    }

}
