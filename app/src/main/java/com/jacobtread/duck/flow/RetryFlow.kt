package com.jacobtread.duck.flow

import androidx.compose.runtime.*
import com.jacobtread.duck.components.Loader
import com.jacobtread.duck.components.RetryLayout

class RetryState {
    var execute by mutableStateOf(true)
    var complete by mutableStateOf(false)
    var failed by mutableStateOf(false)
    var failedMessage by mutableStateOf("")
}

@Composable
fun rememberRetryFlowState(): RetryState {
    return remember { RetryState() }
}

@Composable
fun RetryFlow(
    load: suspend () -> Unit,
    errorTitle: String,
    loadingTitle: String,
    loadingMessage: String,
    state: RetryState = rememberRetryFlowState(),
    manualComplete: Boolean = false,
    content: @Composable () -> Unit,
) {
    LaunchedEffect(state.execute) {
        if (state.execute) {
            try {
                load()
            } catch (e: Throwable) {
                state.failed = true
                state.failedMessage = e.message ?: e.javaClass.simpleName
            }
            if (!manualComplete) state.complete = true
            state.execute = false
        }
    }
    if (state.complete) {
        content()
    } else if (state.failed || !state.execute) {
        RetryLayout(errorTitle, state.failedMessage) {
            state.failed = false
            state.execute = true
        }
    } else {
        Loader(loadingTitle, loadingMessage)
    }
}