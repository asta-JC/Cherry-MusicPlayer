package com.asta.cherrymusicplayer.ui

import com.asta.cherrymusicplayer.data.preferences.ThemeMode
import com.asta.cherrymusicplayer.ui.component.SeekGesture
import com.asta.cherrymusicplayer.ui.viewmodel.PlaybackProgress
import org.junit.Assert.*
import org.junit.Test

class PlaybackInteractionTest {
    @Test fun draggingOnlySeeksOnceOnRelease() {
        val gesture = SeekGesture()
        val calls = mutableListOf<Long>()
        gesture.dragTo(.2f)
        gesture.dragTo(.7f)
        assertTrue(calls.isEmpty())
        assertEquals(.7f, gesture.fraction!!, .001f)
        gesture.release(10000L, calls::add)
        gesture.release(10000L, calls::add)
        assertEquals(listOf(7000L), calls)
        assertNull(gesture.fraction)
    }

    @Test fun seekingBoundsAndUnknownDurationAreSafe() {
        val gesture = SeekGesture()
        val calls = mutableListOf<Long>()
        gesture.dragTo(2f)
        gesture.release(1000, calls::add)
        gesture.dragTo(.5f)
        gesture.release(0, calls::add)
        assertEquals(listOf(1000L), calls)
        assertNull(gesture.fraction)
        assertEquals(0f, PlaybackProgress(100, 0).fraction, 0f)
        assertEquals(1f, PlaybackProgress(200, 100).fraction, 0f)
        assertEquals(0f, PlaybackProgress(-20, 100).fraction, 0f)
    }

    @Test fun themeValuesRoundTripAndUnknownValuesFollowSystem() {
        ThemeMode.entries.forEach { assertEquals(it, ThemeMode.fromStored(it.name)) }
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromStored(null))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromStored("unknown"))
    }
}
