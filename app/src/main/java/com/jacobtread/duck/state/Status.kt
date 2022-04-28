package com.jacobtread.duck.state

import androidx.compose.ui.graphics.Color
import com.jacobtread.duck.theme.ConnectedColor
import com.jacobtread.duck.theme.ErrorColor
import com.jacobtread.duck.theme.RunningColor
import com.jacobtread.duck.theme.WaitingColor

interface Status {
    fun text(): String
    fun color(): Color
}

class RunningStatus(private val script: String) : Status {
    fun script(): String = script
    override fun text(): String = "Running \"$script\""
    override fun color(): Color = RunningColor
}

class ConnectedStatus : Status {
    override fun text(): String = "Connected"
    override fun color(): Color = ConnectedColor
}

class ErrorStatus : Status {
    override fun text(): String = "Connection Problem"
    override fun color(): Color = ErrorColor
}

class WaitingStatus : Status {
    override fun text(): String = "Waiting"
    override fun color(): Color = WaitingColor
}