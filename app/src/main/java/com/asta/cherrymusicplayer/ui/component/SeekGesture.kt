package com.asta.cherrymusicplayer.ui.component

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Keeps finger feedback independent of periodic playback updates. */
class SeekGesture {
    var fraction: Float? by mutableStateOf(null)
        private set

    fun dragTo(value: Float) { fraction = value.coerceIn(0f, 1f) }

    fun release(duration: Long, seek: (Long) -> Unit) {
        val dragged = fraction
        fraction = null
        if (dragged != null && duration > 0) seek((dragged * duration).toLong().coerceIn(0, duration))
    }
}
