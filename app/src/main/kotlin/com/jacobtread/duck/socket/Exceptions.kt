package com.jacobtread.duck.socket

import io.ktor.websocket.*

class InvalidResponse(message: String) : RuntimeException(message)
class UnexpectedlyClosed(reason: CloseReason?) : RuntimeException("Socket unexpected closed: ${reason?.message ?: "Unknown reason"} (${reason?.code ?: "Unknown code"})")