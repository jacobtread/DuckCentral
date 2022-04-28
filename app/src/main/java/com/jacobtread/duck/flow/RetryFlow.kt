package com.jacobtread.duck.flow

import androidx.compose.runtime.*
import com.jacobtread.duck.components.Loader
import com.jacobtread.duck.components.RetryLayout

class RetryState {
    var waiting by mutableStateOf(true)
    var failed by mutableStateOf(false)
    var failedMessage by mutableStateOf("")
}

@Composable
fun RetryFlow(
    load: suspend () -> Unit,
    errorTitle: String,
    loadingTitle: String,
    loadingMessage: String,
    content: @Composable () -> Unit,
) {
    val state = remember { RetryState() }

    LaunchedEffect(state.waiting) {
        if (state.waiting) {
            try {
               load()
            } catch (e: Throwable) {
                state.failed = true
                state.failedMessage = e.message ?: e.javaClass.simpleName
            }
            state.waiting = false
        }
    }

    if (state.failed) {
        RetryLayout(errorTitle, state.failedMessage) {
            state.failed = false
            state.waiting = true
        }
    } else if (state.waiting) {
        Loader(loadingTitle, loadingMessage)
    } else {
        content()
    }
}