package com.jacobtread.duck.socket

fun interface CommandHandler<V> {
    fun handle(result: Result<V>)
}