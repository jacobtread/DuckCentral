package com.jacobtread.duck.socket.command

import com.jacobtread.duck.socket.InvalidResponseException
import io.ktor.websocket.*

/**
 * command Sends the provided command and receives the
 * response value back returning it
 *
 * @param R The response type
 * @param command The command to send
 * @return The response
 */
suspend fun <R> WebSocketSession.command(command: Command<R>): R {
    command.send(this)
    return command.receive(this)
}

/**
 * command Sends a command to the websocket server. Commands
 * must end with a new line character if they don't one is
 * appended to them
 *
 * @param value The command to be sent
 */
suspend fun WebSocketSession.command(value: String) {
    if (value.endsWith("\n")) send(value) else send("$value\n")
}

/**
 * text Reads the next text frame from the web socket. If the
 * next frame is not a text frame an InvalidResponse exception
 * will be thrown
 *
 * @return The resulting text that was read
 */
@Throws(InvalidResponseException::class)
suspend fun WebSocketSession.text(): String {
    val frame: Frame = incoming.receive()
    if (frame !is Frame.Text) throw InvalidResponseException("Expected text frame but got ${frame.frameType} instead")
    return frame.readText()
}

/**
 * textSplit Reads text lines that are split by the new line
 * character. If a limit is provided then the values will
 * ensure they remove any trailing new line characters from
 * the last value
 *
 * @param limit The limit of lines to split -1 for unlimited
 * @return The resulting split lines
 */
suspend fun WebSocketSession.textSplit(limit: Int = -1): List<String> {
    val text = text()
    return if (limit == -1) {
        text.split('\n')
            .filter { it.isNotBlank() }
            .toList()
    } else {
        text.split('\n', limit = limit)
            .filter { it.isNotBlank() }
            .map { line -> line.trimEnd { it == '\n' } }
            .toList()
    }
}

/**
 * stream Reads a stream of data provided by the server
 * will stop reading when it reaches a frame that contains
 * the > END text
 *
 * @return The combined string of all the read data
 */
suspend fun WebSocketSession.stream(): String {
    val output = StringBuilder()
    var line: String
    while (true) {
        command("read")
        line = text() // Take the next text frame
        if (line == "> END") break // Break when the end frame is received
        output.append(line)
    }
    command("close")
    return output.toString()
}

/**
 * stream Streams the provided content to the provided file
 * the contents string is written in chunks of 1024 bytes until
 * its complete then the close frame is sent
 *
 * @param file The file to stream to
 * @param content The content to stream to the file
 */
suspend fun WebSocketSession.stream(file: String, content: String) {
    command("stream \"$file\"")
    content.chunked(1024).forEach { send(it) }
    command("close")
}