package com.jacobtread.duck.api

import androidx.compose.runtime.mutableStateOf
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

enum class SocketState {
    Waiting,
    Connecting
}

object Socket {


    var statusUpdates: Boolean = true;

    val queue = MessageQueue()
    const val HOST_ADDR = "192.168.4.1"
    const val HOST_PORT = 80


    fun connect() {
        val client = HttpClient(CIO) {
            install(WebSockets)
        }
        Thread {
            runBlocking {
                withContext(Dispatchers.IO) {
                    client.webSocket(method = HttpMethod.Get, host = HOST_ADDR, port = HOST_PORT) {

                        while (true) {

                        }
                    }
                }
            }
        }.apply {
            name = "Socket Thread"
            start()
        }
    }

}