package com.jacobtread.duck.api

import io.ktor.websocket.*

interface Message<R> {

    suspend fun send(session: WebSocketSession);

    suspend fun receive(session: WebSocketSession): R

}

suspend fun WebSocketSession.readText(): String {
    val msg = incoming.receive() as? Frame.Text
        ?: throw InvalidResponse("Wasn't expecting non text frame");
    return msg.readText();
}

suspend inline fun WebSocketSession.readStream(end: String): String {
    val output = StringBuilder()
    var line: String
    while(true) {
        line = readText()
        if (line == end) break
        output.append(line)
    }
    return output.toString()
}

class InvalidResponse(message: String) : RuntimeException(message)

data class MemoryResponse(val totalBytes: Int, val usedBytes: Int, val freeBytes: Int)

class MemoryRequest : Message<MemoryResponse> {
    override suspend fun send(session: WebSocketSession) {
        session.send("mem")
    }

    override suspend fun receive(session: WebSocketSession): MemoryResponse {
        val text = session.readText()
        val lines = text.split('\n', limit = 3)
        if (lines.size < 3) throw InvalidResponse("Incomplete memory response")
        return MemoryResponse(
            parseValue(lines[0]),
            parseValue(lines[1]),
            parseValue(lines[2]),
        )
    }
    private fun parseValue(input: String): Int {
        val parts = input.split(' ', limit = 2)
        if (parts.isEmpty()) throw InvalidResponse("Incomplete memory response")
        return parts[0].toIntOrNull() ?: throw InvalidResponse("Invalid memory value")
    }
}