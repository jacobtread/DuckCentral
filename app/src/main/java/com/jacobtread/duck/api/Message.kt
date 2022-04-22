package com.jacobtread.duck.api

import io.ktor.websocket.*

/**
 * Message Represents a message that can be sent to the
 * WebSocket server that will also read a response from
 * the socket server
 *
 * @param R The type of response that will be created
 * @constructor Create empty Message
 */
interface Message<R> {

    /**
     * send Implementations will send the command to the
     * server as plain text packets
     *
     * @param session The session to send the packet to
     */
    suspend fun send(session: WebSocketSession);

    /**
     * receive
     *
     * @param session
     * @return
     */
    suspend fun receive(session: WebSocketSession): R
}

/**
 * SMessage a simple message that will only send a single
 * message text and will always receive a plain
 * text response that doesn't need to be formatted in
 * any special way
 *
 * @constructor Create empty SMessage
 */
open class SimpleMessage(private val message: String) : Message<String> {
    override suspend fun send(session: WebSocketSession) {
        session.writeText(message)
    }

    override suspend fun receive(session: WebSocketSession): String {
        return session.readText()
    }
}

/**
 * sendMessage Sends the provided message to the session and returns
 * the received response
 *
 * @param R The type of the response
 * @param message The message object to send
 * @return The received response
 */
suspend fun <R> WebSocketSession.message(message: Message<R>): R {
    message.send(this)
    return message.receive(this)
}

/**
 * writeText Writes text to the web socket. Text that does
 * not end with a new line will have one appended.
 *
 * @param value The text value to write
 */
suspend fun WebSocketSession.writeText(value: String) {
    if (value.endsWith("\n")) {
        send(value)
    } else {
        send("${value}\n")
    }
}

/**
 * readText Extension function on WebSocketSession for reading
 * strings of text from the websocket connection
 *
 * @throws InvalidResponse thrown if the frame wasn't a text frame
 * @return The text that was read
 */
suspend fun WebSocketSession.readText(): String {
    var iteration = 0
    while (iteration < 10) {
        val msg = incoming.receive();
        if (msg is Frame.Text) return msg.readText()
        else if (msg is Frame.Close) {
            throw UnexpectedlyClosed(msg.readReason())
        }
        iteration++
    }
    throw RuntimeException("Failed to read response from socket. Too many invalid frames.")
}

/**
 * readStream Reads a stream of text from the WebSocketSession
 * stops reading when it is provided a string that matches
 * > END
 *
 * @throws InvalidResponse thrown if one of the frames wasn't a text frame
 * @return The combined output of the entire read stream
 */
suspend fun WebSocketSession.readStream(): String {
    val output = StringBuilder()
    var line: String
    while (true) {
        writeText("read") // Request more data from the server
        line = readText()
        if (line == "> END") break
        output.append(line)
    }
    writeText("close") // Tell the server to close the file stream
    return output.toString()
}

// The maximum size of the chunks that can be streamed
// to the server (1024 bytes per frame)
const val CHUNK_SIZE = 1024

/**
 * writeStream Writes the provided data value as
 * a stream onto the server. A stream must be
 * started before sending data through this
 *
 * @param value The data to stream
 */
suspend fun WebSocketSession.writeStream(to: String, value: String) {
    writeText("stream \"$to\"")
    var cursor = 0
    var length: Int
    var slice: String
    while (cursor < value.length) {
        length = if (cursor + CHUNK_SIZE >= value.length) {
            cursor + CHUNK_SIZE
        } else {
            value.length - cursor
        }
        slice = value.substring(cursor..length)
        send(slice) // Newlines aren't appended to streams
        cursor += length
    }
    writeText("close")
}