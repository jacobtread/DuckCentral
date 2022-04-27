package com.jacobtread.duck.state

import androidx.compose.runtime.mutableStateListOf

object TerminalState {
    val lines = mutableStateListOf<TerminalLine>()
    val end: Int get() = lines.size - 1

    enum class LineType {
        Sent,
        Received,
        Error
    }

    data class TerminalLine(val type: LineType, val content: String)

    fun addLine(type: LineType, value: String) {
        value.split('\n')
            .filter { it.isNotBlank() }
            .map { TerminalLine(type, it) }
            .toCollection(lines)
    }
}