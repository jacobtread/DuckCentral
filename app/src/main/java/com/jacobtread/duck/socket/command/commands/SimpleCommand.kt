package com.jacobtread.duck.socket.command.commands

import com.jacobtread.duck.socket.command.command
import com.jacobtread.duck.socket.command.Command
import com.jacobtread.duck.socket.command.text
import io.ktor.websocket.*

/**
 * SimpleCommand Class for sending simple commands that send a string
 * and receive a string back
 *
 * @constructor Create empty SimpleMessage
 */
open class SimpleCommand(private val message: String) : Command<String> {
    override suspend fun send(session: WebSocketSession) = session.command(message)
    override suspend fun receive(session: WebSocketSession): String = session.text()
}

class VersionCommand : SimpleCommand("version")
class FormatCommand : SimpleCommand("format")