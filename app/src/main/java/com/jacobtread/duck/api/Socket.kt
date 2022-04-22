package com.jacobtread.duck.api

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun interface ResponseConsumer<R> {
    fun consume(value: R)
}

data class QueueItem<R>(val message: Message<R>, val handler: ResponseConsumer<R>)

object Socket {

    private const val HOST_ADDR = "192.168.4.1"
    private const val HOST_PORT = 80
    private const val STATUS_UPDATE_INTERVAL: Long = 1000; // Update status every second

    var statusUpdates: Boolean = true;
    private var lastStatusUpdate: Long = -1;

    private var status by remember { mutableStateOf("waiting") }


    private val queue = ArrayDeque<QueueItem<*>>()

    private val statusHandler: ResponseConsumer<String> = ResponseConsumer { status = it }

    fun pushStatus() = push(StatusMessage(), statusHandler)

    fun <R> push(message: Message<R>, responseHandler: ResponseConsumer<R>) {
        queue.addLast(QueueItem(message, responseHandler))
    }

    private suspend fun run(session: WebSocketSession) {
        while (true) {
            while (queue.isNotEmpty()) {
                val item = queue.removeFirst();
                consumeItem(item, session)
            }
            if (statusUpdates) {
                val time = System.currentTimeMillis();
                if (time - lastStatusUpdate >= STATUS_UPDATE_INTERVAL) {
                    lastStatusUpdate = time;
                    pushStatus()
                }
            }
        }
    }

    /**
     * consumeItem This function only exists to ensure that type
     * checking can successfully link the return type of the
     * message with the consumer input type. Otherwise, Kotlin will
     * assume the type as Any? causing type checking to fail
     *
     * @param R The response type
     * @param item The item to consume
     * @param session The session to consume on
     */
    private suspend inline fun <R> consumeItem(item: QueueItem<R>, session: WebSocketSession) {
        val response = session.message(item.message)
        item.handler.consume(response)
    }

    /**
     * connect Connects to the web socket server
     *
     * @param host The host of the web socket server
     * @param port The port of the web socket server
     */
    fun connect(host: String = HOST_ADDR, port: Int = HOST_PORT) {
        val client = HttpClient(CIO) {
            install(WebSockets)
        }
        Thread {
            runBlocking {
                withContext(Dispatchers.IO) {
                    client.webSocket(method = HttpMethod.Get, host = host, port = port) {
                        run(this)
                    }
                }
            }
        }.apply {
            name = "Socket Thread"
            start()
        }
    }

}