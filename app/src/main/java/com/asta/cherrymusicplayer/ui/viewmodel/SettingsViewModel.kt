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
    val equalizerPreset: String = "Normal",
    val sleepTimerMinutes: Int? = null,
    val sleepTimerRemainingMillis: Long? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val playerViewModel: PlayerViewModel
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        preferencesManager.darkThemeGlowFlow,
        preferencesManager.playbackSpeedFlow,
        preferencesManager.equalizerPresetFlow,
        playerViewModel.uiState
    ) { glow, speed, eq, playerState ->
        SettingsUiState(
            darkThemeGlow = glow,
            playbackSpeed = speed,
            equalizerPreset = eq,
            sleepTimerMinutes = playerState.sleepTimerMinutes,
            sleepTimerRemainingMillis = playerState.sleepTimerRemainingMillis
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

    fun setPlaybackSpeed(speed: Float) {
        playerViewModel.setPlaybackSpeed(speed)
    }

    fun setEqualizerPreset(preset: String) {
        viewModelScope.launch {
            preferencesManager.setEqualizerPreset(preset)
        }
    }

    fun setSleepTimer(minutes: Int) {
        playerViewModel.setSleepTimer(minutes)
    }
}
