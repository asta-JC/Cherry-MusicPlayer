package com.asta.cherrymusicplayer.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cherry_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val DARK_THEME_GLOW = booleanPreferencesKey("dark_theme_glow")
        val LAST_PLAYED_SONG_ID = longPreferencesKey("last_played_song_id")
        val SHUFFLE_MODE = booleanPreferencesKey("shuffle_mode")
        val REPEAT_MODE = intPreferencesKey("repeat_mode") // 0: off, 1: one, 2: all
        val PLAYBACK_SPEED = floatPreferencesKey("playback_speed")
        val LAST_PLAYBACK_POSITION = longPreferencesKey("last_playback_position")
        val EQUALIZER_PRESET = stringPreferencesKey("equalizer_preset")
        val MIN_SONG_DURATION = longPreferencesKey("min_song_duration_ms")
    }

    val darkThemeGlowFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.DARK_THEME_GLOW] ?: true }

    val lastPlayedSongIdFlow: Flow<Long> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.LAST_PLAYED_SONG_ID] ?: -1L }

    val shuffleModeFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.SHUFFLE_MODE] ?: false }

    val repeatModeFlow: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.REPEAT_MODE] ?: 0 }

    val playbackSpeedFlow: Flow<Float> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.PLAYBACK_SPEED] ?: 1.0f }

    val lastPlaybackPositionFlow: Flow<Long> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.LAST_PLAYBACK_POSITION] ?: 0L }

    val equalizerPresetFlow: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.EQUALIZER_PRESET] ?: "Normal" }

    val minSongDurationFlow: Flow<Long> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.MIN_SONG_DURATION] ?: 100_000L }

    suspend fun setDarkThemeGlow(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_THEME_GLOW] = enabled
        }
    }

    suspend fun setLastPlayedSongId(songId: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_PLAYED_SONG_ID] = songId
        }
    }

    suspend fun setShuffleMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHUFFLE_MODE] = enabled
        }
    }

    suspend fun setRepeatMode(mode: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REPEAT_MODE] = mode
        }
    }

    suspend fun setPlaybackSpeed(speed: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PLAYBACK_SPEED] = speed
        }
    }

    suspend fun setLastPlaybackPosition(position: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_PLAYBACK_POSITION] = position
        }
    }

    suspend fun setEqualizerPreset(preset: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EQUALIZER_PRESET] = preset
        }
    }

    suspend fun setMinSongDuration(durationMs: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.MIN_SONG_DURATION] = durationMs
        }
    }
}
