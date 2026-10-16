package com.asta.cherrymusicplayer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asta.cherrymusicplayer.data.preferences.PreferencesManager
import com.asta.cherrymusicplayer.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val darkThemeGlow: Boolean = true,
    val playbackSpeed: Float = 1.0f,
    val equalizerPreset: String = "Normal",
    val isScanning: Boolean = false,
    val scanStatusMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _isScanning = MutableStateFlow(false)
    private val _scanStatusMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SettingsUiState> = combine(
        preferencesManager.darkThemeGlowFlow,
        preferencesManager.playbackSpeedFlow,
        preferencesManager.equalizerPresetFlow,
        _isScanning,
        _scanStatusMessage
    ) { glow, speed, eq, scanning, msg ->
        SettingsUiState(
            darkThemeGlow = glow,
            playbackSpeed = speed,
            equalizerPreset = eq,
            isScanning = scanning,
            scanStatusMessage = msg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setDarkThemeGlow(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDarkThemeGlow(enabled)
        }
    }

    fun setEqualizerPreset(preset: String) {
        viewModelScope.launch {
            preferencesManager.setEqualizerPreset(preset)
        }
    }

    fun scanDevice() {
        viewModelScope.launch {
            _isScanning.value = true
            _scanStatusMessage.value = "Scanning device for music files..."
            try {
                val songs = musicRepository.scanDeviceAudio()
                _scanStatusMessage.value = "Scan complete! Found ${songs.size} music tracks."
            } catch (e: Exception) {
                _scanStatusMessage.value = "Scan failed: ${e.message}"
            } finally {
                _isScanning.value = false
            }
        }
    }

    fun unhideAllSongs() {
        viewModelScope.launch {
            try {
                musicRepository.unhideAllSongs()
                _scanStatusMessage.value = "All hidden tracks have been restored to your library."
            } catch (e: Exception) {
                _scanStatusMessage.value = "Failed to unhide songs: ${e.message}"
            }
        }
    }

    fun clearScanMessage() {
        _scanStatusMessage.value = null
    }
}

