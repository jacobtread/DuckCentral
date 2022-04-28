package com.jacobtread.duck.socket.command.commands

import com.jacobtread.duck.socket.command.command
import com.jacobtread.duck.socket.command.Command
import com.jacobtread.duck.socket.command.text
import com.jacobtread.duck.state.ConnectedStatus
import com.jacobtread.duck.state.ErrorStatus
import com.jacobtread.duck.state.RunningStatus
import com.jacobtread.duck.state.Status
import io.ktor.websocket.*

/**
 * StatusCommand is a command for retrieving the status of the
 * WifiDuck
 *
 * @constructor Create empty StatusCommand
 */
class StatusCommand : Command<Status> {

    override suspend fun send(session: WebSocketSession) {
        session.command("status")
    }

    override suspend fun receive(session: WebSocketSession): Status {
        val status = session.text()
        return if (status.startsWith("running")) {
            val script = status.removePrefix("running ")
            RunningStatus(script)
        } else if (status == "Internal connection problem") {
            ErrorStatus()
        } else {
            ConnectedStatus()
        }
    }
}