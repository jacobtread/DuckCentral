package com.jacobtread.duck.socket

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jacobtread.duck.flow.RetryState
import com.jacobtread.duck.socket.command.Command
import com.jacobtread.duck.socket.command.command
import com.jacobtread.duck.state.Status
import com.jacobtread.duck.state.WaitingStatus
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DuckController {

    // Default host of websocket server
    private const val DEFAULT_HOST = "192.168.4.1"

    // Default port of websocket server
    private const val DEFAULT_PORT = 80

    // The path of the web server the websocket is at
    private const val DEFAULT_PATH = "/ws"


    // The network client used to make calls
    private val client = HttpClient(CIO) { install(WebSockets) }

    // The active web socket session
    private var session: DefaultWebSocketSession? = null

    // Whether we are connected
    var connected = RetryState()

    // The thread for updating status
    private val statusThread = StatusThread(this)

    var lastStatus: Status by mutableStateOf(WaitingStatus())

    /**
     * connect Connects to a websocket connection disconnecting
     * the connection if there is already one. Also starts the
     * status thread if it's not already started
     *
     * @param host The host of the websocket server
     * @param port The port of the websocket server
     * @param path The path to the websocket server endpoint
     */
    suspend fun connect(
        host: String = DEFAULT_HOST,
        port: Int = DEFAULT_PORT,
        path: String = DEFAULT_PATH,
    ) {
        // Disconnect if we are already connected
        if (connected.complete) disconnect()
        withContext(Dispatchers.IO) {
            // Create a new session
            session = client.webSocketSession(HttpMethod.Get, host, port, path)
            connected.complete = true
            statusThread.start()
        }
    }

    /**
     * disconnect Disconnects the current websocket connection
     * if there is one active and clears the active session
     */
    suspend fun disconnect() {
        withContext(Dispatchers.IO) {
            session?.close(CloseReason(CloseReason.Codes.NORMAL, "Disconnecting"))
            session = null
            connected.complete = false
        }
    }

    /**
     * send Sends the provided command and waits for a
     * response from the websocket then returns that response
     *
     * @param R The type of the response
     * @param command The command to send
     * @return The response for the message
     */
    @Throws(InvalidResponseException::class, NotConnectedException::class)
    suspend fun <R> send(command: Command<R>): R {
        val session = this.session ?: throw NotConnectedException()
        return withContext(Dispatchers.IO) { session.command(command) }
    }

}