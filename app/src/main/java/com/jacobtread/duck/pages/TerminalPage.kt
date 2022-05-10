package com.jacobtread.duck.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.jacobtread.duck.socket.DuckSocket
import com.jacobtread.duck.socket.command.commands.SimpleCommand
import com.jacobtread.duck.theme.TerminalErrorColor
import com.jacobtread.duck.theme.TerminalReceivedColor
import com.jacobtread.duck.theme.TerminalSentColor
import kotlinx.coroutines.launch

object TerminalPage : Page("Terminal", "terminal", Icons.Filled.Terminal) {

    enum class LineType {
        Sent,
        Received,
        Error
    }

    data class TerminalLine(val type: LineType, val content: String)

    @Composable
    override fun Content(navController: NavHostController, stackEntry: NavBackStackEntry) {
        val lines = remember { mutableStateListOf<TerminalLine>() }
        val scrollState = rememberLazyListState()
        Column {
            Messages(scrollState, lines, Modifier
                .padding(vertical = 15.dp)
                .fillMaxSize()
                .weight(1f)
            )
            UserInput(scrollState, lines)
        }
    }

    @Composable
    fun Messages(scrollState: LazyListState, lines: List<TerminalLine>, modifier: Modifier) {
        Surface(
            modifier,
            color = Color.Black
        ) {
            LazyColumn(
                Modifier.padding(15.dp),
                scrollState
            ) {
                for (index in lines.indices) {
                    val line = lines[index]
                    item {
                        when (line.type) {
                            LineType.Received -> Text(line.content, fontFamily = FontFamily.Monospace, color = TerminalReceivedColor)
                            LineType.Sent -> Text("$ ${line.content}", fontFamily = FontFamily.Monospace, color = TerminalSentColor)
                            LineType.Error -> Text(line.content, fontFamily = FontFamily.Monospace, color = TerminalErrorColor)
                        }
                    }
                }
            }
        }
    }

    private fun MutableList<TerminalLine>.add(type: LineType, value: String) {
        value.split('\n')
            .filter { it.isNotBlank() }
            .map { TerminalLine(type, it) }
            .toCollection(this)
    }

    @Composable
    fun UserInput(scrollState: LazyListState, lines: MutableList<TerminalLine>) {
        var sending by remember { mutableStateOf(false) }
        var message by remember { mutableStateOf("") }
        val scope = rememberCoroutineScope()
        TextField(
            value = message,
            onValueChange = { message = it },
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
                                lines.add(LineType.Sent, message)
                                scope.launch {
                                    try {
                                        val result = DuckSocket.send(SimpleCommand(message))
                                        lines.add(LineType.Received, result)
                                        message = ""
                                        sending = false
                                    } catch (e: Throwable) {
                                        lines.add(LineType.Error, "ERROR: ${e.message ?: e.javaClass.simpleName}")
                                    }
                                    if (!scrollState.isScrollInProgress) {
                                        scrollState.animateScrollToItem(lines.size - 1)
                                    }
                                }
                            }
                        },
                        content = { Icon(Icons.Filled.Send, null) }
                    )
                    IconButton(
                        enabled = lines.size > 0,
                        onClick = { lines.clear() },
                        content = { Icon(Icons.Filled.Delete, null) }
                    )
                }
            }
        )
    }
}