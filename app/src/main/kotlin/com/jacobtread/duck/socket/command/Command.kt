package com.jacobtread.duck.socket.command

import io.ktor.websocket.*

/**
 * Command Represents a message that can be sent through a websocket
 * connection and receive a response after sending
 *
 * @param R The type of response to expect back
 * @constructor Create empty Message
 */
interface Command<R> {

    /**
     * send Writes the contents of this message to
     * the provided session
     *
     * @param session  The session to write to
     */
    suspend fun send(session: WebSocketSession)

    /**
     * receive Receives (Reads) the response value of R from
     * the provided websocket session and returns it
     *
     * @param session The session to read from
     * @return The value that was read
     */
    suspend fun receive(session: WebSocketSession): R
}