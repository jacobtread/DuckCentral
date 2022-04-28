package com.jacobtread.duck.socket

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class DuckController {

    companion object {
        // Default host of websocket server
        private const val DEFAULT_HOST = "192.168.4.1"

        // Default port of websocket server
        private const val DEFAULT_PORT = 80

        // The path of the web server the websocket is at
        private const val DEFAULT_PATH = "/ws"
    }

    // Dedicated thread for executing duck actions
    private val statusExecutor = Executors.newSingleThreadExecutor()

    // The network client used to make calls
    private val client = HttpClient(CIO) { install(WebSockets) }

    private var session: DefaultWebSocketSession? = null


    suspend fun connect(
        host: String = DEFAULT_HOST,
        port: Int = DEFAULT_PORT,
        path: String = DEFAULT_PATH,
    ) {
        withContext(Dispatchers.IO) {
            // Create a new session
            session = client.webSocketSession(HttpMethod.Get, host, port, path)
        }
    }

    suspend fun disconnect() {
        withContext(Dispatchers.IO) {
            session?.close(CloseReason(CloseReason.Codes.NORMAL, "Disconnecting"))
        }
    }

}