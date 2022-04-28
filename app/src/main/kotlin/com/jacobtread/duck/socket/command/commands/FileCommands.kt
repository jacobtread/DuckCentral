package com.jacobtread.duck.socket.command.commands

import com.jacobtread.duck.socket.InvalidResponseException
import com.jacobtread.duck.socket.command.*
import io.ktor.websocket.*

fun cleanFileName(name: String): String {
    if (name.isEmpty()) return name
    val fixed = name.replace(' ', '-');
    return if (fixed[0] != '/') {
        "/$fixed"
    } else {
        fixed
    }
}

class CreateFileCommand(name: String) : Command<String> {
    private val name: String = cleanFileName(name)
    override suspend fun send(session: WebSocketSession) {
        session.command(StopScriptCommand(name))
        session.command("create \"$name\"")
    }

    override suspend fun receive(session: WebSocketSession): String = session.text()
}

class DeleteFileCommand(name: String) : Command<String> {
    private val name: String = cleanFileName(name)
    override suspend fun send(session: WebSocketSession) {
        session.command(StopScriptCommand(name))
        session.command("remove \"$name\"")
    }

    override suspend fun receive(session: WebSocketSession): String = session.text()
}

class RenameFileCommand(file1: String, file2: String) : Command<String> {
    private val file1: String = cleanFileName(file1)
    private val file2: String = cleanFileName(file2)
    override suspend fun send(session: WebSocketSession) = session.command("rename \"$file1\" \"$file2\"")
    override suspend fun receive(session: WebSocketSession): String = session.text()
}

const val TEMP_FILE_NAME = "/temporary_script"

class WriteFileCommand(name: String, private val content: String) : Command<Unit> {
    private val name: String = cleanFileName(name)

    override suspend fun send(session: WebSocketSession) {
        session.command(DeleteFileCommand(TEMP_FILE_NAME))
        session.command(CreateFileCommand(TEMP_FILE_NAME))
        session.stream(TEMP_FILE_NAME, content)
        session.command(DeleteFileCommand(name))
        session.command(RenameFileCommand(TEMP_FILE_NAME, name))
    }

    override suspend fun receive(session: WebSocketSession) {}
}

class ReadFileCommand(name: String) : Command<String> {
    private val name: String = cleanFileName(name)
    override suspend fun send(session: WebSocketSession) = session.command("stream \"$name\"")
    override suspend fun receive(session: WebSocketSession): String = session.stream()
}

/**
 * FileResponse Represents a file stored on the spiff file system
 *
 * @property name The name of the file
 * @property size The size of the file
 * @constructor Create empty File
 */
data class FileResponse(val name: String, val size: Int)

/**
 * Type alias for a list of file responses
 */
typealias FilesResponse = List<FileResponse>

class FilesCommand : Command<FilesResponse> {
    override suspend fun send(session: WebSocketSession) = session.command("ls")
    override suspend fun receive(session: WebSocketSession): FilesResponse {
        val contents = session.textSplit()
        return contents.map {
            val parts = it.split(' ', limit = 2)
            if (parts.size < 2) throw InvalidResponseException("File list response contained invalid number of parts")
            val size = parts[1].toIntOrNull() ?: throw InvalidResponseException("File size was not a number")
            FileResponse(parts[0], size)
        }
    }
}

/**
 * StorageUsage
 *
 * @property totalBytes The total bytes of storage available
 * @property usedBytes The number of bytes of storage that are in use
 * @property freeBytes The number of bytes that are free
 * @constructor Create empty StorageUsage
 */
data class StorageUsage(val totalBytes: Int, val usedBytes: Int, val freeBytes: Int)

class StorageUsageCommand : Command<StorageUsage> {
    override suspend fun send(session: WebSocketSession) = session.command("mem")
    override suspend fun receive(session: WebSocketSession): StorageUsage {
        val lines = session.textSplit(3)
        if (lines.size < 3) throw InvalidResponseException("Incomplete memory response invalid length")
        return StorageUsage(
            parseValue(lines[0]),
            parseValue(lines[1]),
            parseValue(lines[2]),
        )
    }

    private fun parseValue(value: String): Int {
        val size = value.substringBefore(' ')
        return size.toIntOrNull() ?: throw InvalidResponseException("Invalid storage size: $size")
    }

}

class RamCommand : Command<Int> {
    override suspend fun send(session: WebSocketSession) = session.command("ram")
    override suspend fun receive(session: WebSocketSession): Int {
        val value = session.text()
        val size = value.substringBefore(' ')
        return size.toIntOrNull() ?: throw InvalidResponseException("Invalid ram free bytes: $size")
    }
}