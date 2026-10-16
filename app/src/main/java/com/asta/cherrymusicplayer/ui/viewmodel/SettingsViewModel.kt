package com.asta.cherrymusicplayer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asta.cherrymusicplayer.data.preferences.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val darkThemeGlow: Boolean = true,
    val playbackSpeed: Float = 1.0f,
    val equalizerPreset: String = "Normal"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        preferencesManager.darkThemeGlowFlow,
        preferencesManager.playbackSpeedFlow,
        preferencesManager.equalizerPresetFlow
    ) { glow, speed, eq ->
        SettingsUiState(
            darkThemeGlow = glow,
            playbackSpeed = speed,
            equalizerPreset = eq
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
}
