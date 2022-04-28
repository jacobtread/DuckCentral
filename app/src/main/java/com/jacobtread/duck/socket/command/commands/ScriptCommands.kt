package com.jacobtread.duck.socket.command.commands

import com.jacobtread.duck.socket.command.command
import com.jacobtread.duck.socket.command.Command
import com.jacobtread.duck.socket.command.text
import io.ktor.websocket.*

class StopScriptCommand(name: String) : Command<String> {
    private val name: String = cleanFileName(name)
    override suspend fun send(session: WebSocketSession) = session.command("stop \"$name\"")
    override suspend fun receive(session: WebSocketSession): String = session.text()
}

class RunScriptCommand(name: String) : Command<String> {
    private val name: String = cleanFileName(name)
    override suspend fun send(session: WebSocketSession) = session.command("run \"$name\"")
    override suspend fun receive(session: WebSocketSession): String = session.text()
}