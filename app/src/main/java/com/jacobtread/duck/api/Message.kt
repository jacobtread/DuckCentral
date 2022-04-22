package com.jacobtread.duck.api

interface Message<R> {

    fun build(): String;

    fun parse(input: String): R
}

abstract class StreamedMessage<R> : Message<R> {

    val contents = StringBuilder();

    fun consume(input: String): Boolean {
        if (input == "> END") return true;
        contents.append(input);
        return false
    }
}
