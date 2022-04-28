package com.jacobtread.duck.socket

import io.ktor.websocket.*

class InvalidResponseException(message: String) : RuntimeException(message)
class NotConnectedException() : RuntimeException("There is no active websocket connection")