package com.jacobtread.duck.api

import io.ktor.websocket.*

/**
 * StorageUsage
 *
 * @property totalBytes The total bytes of storage available
 * @property usedBytes The number of bytes of storage that are in use
 * @property freeBytes The number of bytes that are free
 * @constructor Create empty StorageUsage
 */
data class StorageUsage(val totalBytes: Int, val usedBytes: Int, val freeBytes: Int)

/**
 * Settings
 *
 * @property ssid The SSID of the access point the chip is broadcasting
 * @property password The password to the access point
 * @property channel The channel the access point is broadcasting on
 * @property autorun The name of the script that is being run on startup
 * @constructor Create empty Settings
 */
data class Settings(
    val ssid: String,
    val password: String,
    val channel: String,
    val autorun: String,
)

/**
 * Type alias for a list of files
 */
typealias FileList = List<File>

/**
 * File Represents a file stored on the spiff file system
 *
 * @property name The name of the file
 * @property size The size of the file
 * @constructor Create empty File
 */
data class File(val name: String, val size: Int)

/**
 * StorageMessage Requests the storage usage statistics from
 * the websocket server. This will return a StorageUsage object
 * containing the used space in bytes
 *
 * @constructor Create empty StorageMessage
 */
class StorageMessage : Message<StorageUsage> {
    override suspend fun send(session: WebSocketSession) {
        session.writeText("mem")
    }

    override suspend fun receive(session: WebSocketSession): StorageUsage {
        val text = session.readText()
        val lines = text.split('\n', limit = 3)
        if (lines.size < 3) throw InvalidResponse("Incomplete memory response")
        return StorageUsage(
            parseValue(lines[0]),
            parseValue(lines[1]),
            parseValue(lines[2]),
        )
    }

    private fun parseValue(input: String): Int {
        val parts = input.split(' ', limit = 2)
        if (parts.isEmpty()) throw InvalidResponse("Incomplete memory response")
        return parts[0].toIntOrNull() ?: throw InvalidResponse("Invalid memory value")
    }
}

/**
 * RamMessage Requests the current ram usage of the ESP chip
 * from the web socket server. Returns the number of free bytes
 *
 * @constructor Create empty RamMessage
 */
class RamMessage : Message<Int> {
    override suspend fun send(session: WebSocketSession) {
        session.writeText("ram")
    }

    override suspend fun receive(session: WebSocketSession): Int {
        val parts = session.readText()
            .split(' ', limit = 2)
        if (parts.size < 2) throw InvalidResponse("Ram response invalid")
        return parts[0].toIntOrNull()
            ?: throw InvalidResponse("Expected ram usage to be a number")
    }
}

/**
 * StatusMessage Requests the status of the server from
 * the websocket. This will be one of the following
 *
 * running {SCRIPT_NAME}
 * connected
 * Internal connection problem
 *
 *
 * @constructor Create empty StatusMessage
 */
class StatusMessage : Message<String> {
    override suspend fun send(session: WebSocketSession) {
        session.writeText("status")
    }

    override suspend fun receive(session: WebSocketSession): String {
        return session.readText()
    }
}

/**
 * FilesMessage Requests a list of stored files from
 * the web socket server. This includes the name
 * and size of the files.
 *
 * @constructor Create empty FilesMessage
 */
class FilesMessage : Message<FileList> {
    override suspend fun send(session: WebSocketSession) {
        session.writeText("ls")
    }

    override suspend fun receive(session: WebSocketSession): FileList {
        val message = session.readText()
        val lines = message.split('\n')
        val files = ArrayList<File>()
        lines.filter {it.isNotBlank() } .map {
            val parts = it.split(' ', limit = 2)
            if (parts.size < 2) throw InvalidResponse("File list response invalid number of parts")
            val size = parts[1].toIntOrNull() ?: throw InvalidResponse("File size was not a number")
            File(parts[0], size)
        }.toCollection(files)
        return files
    }

}

/**
 * FormatMessage Requests that the server format its
 * storage removing all the stored files this will
 * always respond with: "Formatted SPIFFS"
 *
 * @constructor Create empty FormatMessage
 */
class FormatMessage : SimpleMessage("format")

/**
 * VersionMessage Requests the version of both chips
 * from the web socket server the version result
 * will come back as
 *
 * Version {ESP_VERSION} ({ATMEGA_VERSION})
 *
 * @constructor Create empty VersionMessage
 */
class VersionMessage : SimpleMessage("version")

/**
 * SettingsMessage Requests the current settings from the
 * web socket server.
 *
 * @constructor Create empty SettingsMessage
 */
open class SettingsMessage : Message<Settings> {
    override suspend fun send(session: WebSocketSession) {
        session.writeText("settings")
    }

    override suspend fun receive(session: WebSocketSession): Settings {
        val lines = session.readTextSplit(4)
        if (lines.size < 4) throw InvalidResponse("Settings response was missing value")
        return Settings(
            parseValue(lines[0]),
            parseValue(lines[1]),
            parseValue(lines[2]),
            parseValue(lines[3])
        )
    }

    private fun parseValue(value: String): String {
        val parts = value.split('=', limit = 2)
        if (parts.size < 2) throw InvalidResponse("Settings pair was invalid: $value")
        return parts[1]
    }
}

/**
 * ResetMessage Requests that the server reset its settings
 * the server will respond back with the new settings.
 * The parsing behavior is inherited from SettingsMessage
 *
 * @constructor Create empty ResetMessage
 */
class ResetMessage : SettingsMessage() {
    override suspend fun send(session: WebSocketSession) {
        session.writeText("reset")
    }
}

/**
 * SetSettingMessage Requests that the server change the setting
 * with the provided key to the provided value
 *
 * @constructor
 *
 * @param key The key of settings to update
 * @param value The new value for the setting
 */
class SetSettingMessage(key: String, value: String) : SimpleMessage("set $key $value")

const val TEMP_FILE_NAME = "/temporary_script"

/**
 * cleanFileName Cleans up file name so that it can
 * be correctly processed (appends / to the start if its
 * missing and replaces all spaces with -)
 *
 * @param name
 * @return
 */
fun cleanFileName(name: String): String {
    if (name.isEmpty()) return name
    val fixed = name.replace(' ', '-');
    return if (fixed[0] != '/') {
        "/$fixed"
    } else {
        fixed
    }
}

class ReadFileMessage(private val name: String) : Message<String> {
    override suspend fun send(session: WebSocketSession) {
        session.writeText("stream \"${cleanFileName(name)}\"")
    }

    override suspend fun receive(session: WebSocketSession): String {
        return session.readStream()
    }
}

class WriteFileMessage(private val name: String, private val content: String) : Message<Unit> {
    override suspend fun send(session: WebSocketSession) {
        session.message(DeleteFileMessage(TEMP_FILE_NAME))
        session.message(CreateFileMessage(TEMP_FILE_NAME))
        session.writeStream(TEMP_FILE_NAME, content)
        val fileName = cleanFileName(name)
        session.message(DeleteFileMessage(fileName))
        session.message(RenameFileMessage(TEMP_FILE_NAME, fileName))
        DuckController.pushStatus()
    }

    override suspend fun receive(session: WebSocketSession) {}
}

class StopScriptMessage(private val name: String) : SimpleMessage("stop \"${cleanFileName(name)}\"")

class RunScriptMessage(private val name: String) : SimpleMessage("run \"${cleanFileName(name)}\"") {
    override suspend fun send(session: WebSocketSession) {
        super.send(session)
        DuckController.statusUpdates = true;
    }
}

class CreateFileMessage(private val name: String) : SimpleMessage("create \"${cleanFileName(name)}\"") {
    override suspend fun send(session: WebSocketSession) {
        session.message(StopScriptMessage(name))
        super.send(session)
    }
}

class DeleteFileMessage(private val name: String) : SimpleMessage("remove \"${cleanFileName(name)}\"") {
    override suspend fun send(session: WebSocketSession) {
        session.message(StopScriptMessage(name))
        super.send(session)
    }
}

class RenameFileMessage(private val oldName: String, private val newName: String)
    : SimpleMessage("rename \"${cleanFileName(oldName)}\" \"${cleanFileName(newName)}\"")
