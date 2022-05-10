package com.jacobtread.duck.socket

import com.jacobtread.duck.socket.command.commands.StatusCommand
import com.jacobtread.duck.state.ErrorStatus
import kotlinx.coroutines.runBlocking

class StatusThread(private val duckController: DuckController) : Thread("Status Update Thread") {

    companion object {
        // The interval in milliseconds between each status update
        const val STATUS_UPDATE_INTERVAL = 1000L;
    }

    // Whether the thread has already been started
    private var started = false

    override fun start() {
        // Make sure we don't start this thread twice
        if (started) return
        started = true
        super.start()
    }

    override fun run() {
        while (true) {
            try {
                if (duckController.connected.complete) {
                    runBlocking {
                        try {
                            val status = duckController.send(StatusCommand())
                            duckController.lastStatus = status
                        } catch (e: Exception) {
                            duckController.disconnect()
                            duckController.lastStatus = ErrorStatus()
                            e.printStackTrace()
                        }
                    }
                }
                sleep(STATUS_UPDATE_INTERVAL)
            } catch (e: InterruptedException) {
                e.printStackTrace()
                break // Break out of the loop when interrupted
            }
        }
    }
}