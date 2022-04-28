package com.jacobtread.duck.socket.command.commands

import com.jacobtread.duck.socket.InvalidResponseException
import com.jacobtread.duck.socket.command.command
import com.jacobtread.duck.socket.command.Command
import com.jacobtread.duck.socket.command.textSplit
import io.ktor.websocket.*

/**
 * SettingsResponse
 *
 * @property ssid The SSID of the access point the chip is broadcasting
 * @property password The password to the access point
 * @property channel The channel the access point is broadcasting on
 * @property autorun The name of the script that is being run on startup
 * @constructor Create empty Settings
 */
data class SettingsResponse(
    val ssid: String,
    val password: String,
    val channel: String,
    val autorun: String,
)

open class SettingsCommand : Command<SettingsResponse> {
    override suspend fun send(session: WebSocketSession) = session.command("settings")
    override suspend fun receive(session: WebSocketSession): SettingsResponse {
        val parts = session.textSplit(4)
        if (parts.size < 4) throw InvalidResponseException("Settings response was not the right length")
        return SettingsResponse(
            parseValue(parts[0]),
            parseValue(parts[1]),
            parseValue(parts[2]),
            parseValue(parts[3])
        )
    }

    private fun parseValue(value: String): String {
        return value.substringAfter('=')
    }
}

class ResetSettingsCommand : SettingsCommand() {
    override suspend fun send(session: WebSocketSession) = session.command("reset")
}

class SetSettingCommand(key: String, value: String) : SimpleCommand("set $key $value")