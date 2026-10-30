package com.asta.cherrymusicplayer

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.asta.cherrymusicplayer.data.preferences.PreferencesManager
import com.asta.cherrymusicplayer.data.preferences.ThemeMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppearancePersistenceTest {
    @Test fun themePersistsAcrossManagerInstances() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val manager = PreferencesManager(context)
        val previous = manager.themeModeFlow.first()
        try {
            ThemeMode.entries.forEach { mode ->
                manager.setThemeMode(mode)
                assertEquals(mode, PreferencesManager(context).themeModeFlow.first())
            }
        } finally {
            manager.setThemeMode(previous)
        }
    }
}
