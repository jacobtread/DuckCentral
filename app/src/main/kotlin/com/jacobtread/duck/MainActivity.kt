package com.jacobtread.duck

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.jacobtread.duck.socket.DuckController

class MainActivity : ComponentActivity() {

    private var duck: DuckController = DuckController()

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {

        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

}