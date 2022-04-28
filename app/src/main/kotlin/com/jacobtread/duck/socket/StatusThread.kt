package com.jacobtread.duck.socket

import kotlinx.coroutines.runBlocking

class StatusThread(val duckController: DuckController) : Thread("Status Update Thread") {

    companion object {
        // The interval in milliseconds between each status update
        const val STATUS_UPDATE_INTERVAL = 1000L;
    }

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
                runBlocking {}
                sleep(STATUS_UPDATE_INTERVAL)
            } catch (_: InterruptedException) {
                break // Break out of the loop when interrupted
            }
        }
    }
}