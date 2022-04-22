package com.jacobtread.duck.api

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object Socket {

    val queue = MessageQueue()
    const val HOST_ADDR = "192.168.4.1"
    const val HOST_PORT = 80

    suspend fun send<: MessageQueue>(message: MEs)

    fun connect() {
        val client = HttpClient(CIO) {
            install(WebSockets)
        }
        Thread {
            runBlocking {
                withContext(Dispatchers.IO) {
                    client.webSocket(method = HttpMethod.Get, host = HOST_ADDR, port = HOST_PORT) {

                        while (true) {
                            val message = incoming.receive() as? Frame.Text
                            message.readText();
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