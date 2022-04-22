package com.jacobtread.duck.api

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

fun interface ResponseConsumer<R> {
    fun consume(value: R)
}

typealias MessageQueue = ArrayDeque<QueueItem<*>>

data class QueueItem<R>(val message: Message<R>, val handler: ResponseConsumer<R>)

object DuckController {

    private val executor = Executors.newSingleThreadExecutor();
    private val Pool = executor.asCoroutineDispatcher()

    // The default web socket server host
    private const val HOST_ADDR = "192.168.4.1"

    // The default web socket server port
    private const val HOST_PORT = 80

    // Interval between requesting a status update (1 second)
    private const val STATUS_UPDATE_INTERVAL: Long = 1000;

    // Whether status updates should be requested automatically
    var statusUpdates: Boolean = true;


    // The queue of messages to be sent along with their handlers
    private val queue = MessageQueue()

    var stateConsumer: ResponseConsumer<String>? = null

    // The time in milliseconds of the last status update
    private var lastStatusUpdate = -1L

    /**
     * push Pushes a new message to the end of the queue.
     *
     * @param R The response type of the message
     * @param message The message to add to the send queue
     * @param handler The handler for when the message is sent and the response is received
     */
    fun <R> push(message: Message<R>, handler: ResponseConsumer<R>) {
        synchronized(queue) {
            queue.addLast(QueueItem(message, handler))
        }
    }

    /**
     * pushStatus Shortcut method for sending status requests to the server
     * and handling them with status handler which updates the internal state
     */
    fun pushStatus() {
        if (stateConsumer != null) {
            push(StatusMessage(), stateConsumer!!)
        }
    }

    /**
     * connect Connects to the web socket server
     *
     * @param host The host of the web socket server
     * @param port The port of the web socket server
     */
    fun connect(host: String = HOST_ADDR, port: Int = HOST_PORT, callback: (e: Exception?) -> Unit) {
        CoroutineScope(Pool).launch {
            try {
                val client = HttpClient(CIO) { install(WebSockets) }
                client.webSocket(method = HttpMethod.Get, path = "/ws", host = HOST_ADDR, port = port) {
                    callback(null)
                    val session = this;
                    run(session)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback(e)
            }
        }
    }

    /**
     * run Runs the connection loop for the websocket which processes
     * the outbound messages along with the inbound responses
     *
     * @param session The websocket session
     */
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
    private suspend fun <R> consumeItem(item: QueueItem<R>, session: WebSocketSession) {
        val response = session.message(item.message)
        item.handler.consume(response)
    }
}
