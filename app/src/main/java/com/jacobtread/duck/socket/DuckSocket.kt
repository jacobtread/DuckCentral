package com.jacobtread.duck.socket

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jacobtread.duck.flow.RetryState
import com.jacobtread.duck.socket.command.Command
import com.jacobtread.duck.socket.command.command
import com.jacobtread.duck.socket.command.commands.StatusCommand
import com.jacobtread.duck.state.Status
import com.jacobtread.duck.state.WaitingStatus
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object DuckSocket : Thread("DuckSocket") {

    data class QueueItem<R>(val command: Command<R>, val handler: CommandHandler<R>)

    private const val DEFAULT_HOST = "192.168.4.1"
    private const val DEFAULT_PORT = 80
    private const val DEFAULT_PATH = "/ws"
    private const val STATUS_UPDATE_INTERVAL = 1000L;

    @Volatile
    private var running: Boolean = true

    private val client = HttpClient(CIO) { install(WebSockets) }
    private var lastSync: Long = System.currentTimeMillis()

    private var session: WebSocketSession? = null

    var lastStatus: Status by mutableStateOf(WaitingStatus())

    val connected = RetryState()


    private val queue = ArrayDeque<QueueItem<*>>()

    suspend fun <R> send(command: Command<R>): R {
        val channel = Channel<Result<R>>(1)
        queue.add(QueueItem(command) {
            runBlocking {
                channel.send(it)
            }
        })
        val result = channel.receive()
        return result.getOrThrow()
    }

    override fun run() {
        while (running) {
            try {
                val session = this.session ?: continue
                runBlocking {
                    while (queue.isNotEmpty()) {
                        val item = queue.removeFirst()
                        processItem(item)
                    }
                    try {
                        val durationPassed = System.currentTimeMillis() - lastSync
                        if (durationPassed >= STATUS_UPDATE_INTERVAL) {
                            val statusCommand = StatusCommand()
                            statusCommand.send(session)
                            val result = statusCommand.receive(session)
                            lastStatus = result
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        running = false
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                break
            }
        }
    }

    private suspend fun <R> processItem(item: QueueItem<R>) {
        try {
            val result = session!!.command(item.command)
            item.handler.handle(Result.success(result))
        } catch (e: Throwable) {
            item.handler.handle(Result.failure(e))
        }
    }

    suspend fun connect(
        host: String = DEFAULT_HOST,
        port: Int = DEFAULT_PORT,
        path: String = DEFAULT_PATH,
    ) {
        if (connected.complete) disconnect()
        withContext(Dispatchers.IO) {
            // Create a new session
            session = client.webSocketSession(HttpMethod.Get, host, port, path)
            connected.complete = true
            start()
        }
    }

    suspend fun disconnect() {
        running = false
        withContext(Dispatchers.IO) {
            session?.close(CloseReason(CloseReason.Codes.NORMAL, "Disconnecting"))
            session = null
            connected.complete = false
        }
    }

}