package com.asta.cherrymusicplayer.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.asta.cherrymusicplayer.ui.theme.*
import com.asta.cherrymusicplayer.ui.viewmodel.PlayerViewModel
import com.asta.cherrymusicplayer.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    playerViewModel: PlayerViewModel,
    onBackClick: () -> Unit
) {
    val settingsUiState by settingsViewModel.uiState.collectAsState()
    val playerUiState by playerViewModel.uiState.collectAsState()

    val eqPresets = listOf(
        "Normal", "Classical", "Dance", "Flat", "Folk",
        "Heavy Metal", "Hip Hop", "Jazz", "Pop", "Rock"
    )

    val sleepTimerOptions = listOf(
        Pair("Off", 0),
        Pair("15m", 15),
        Pair("30m", 30),
        Pair("45m", 45),
        Pair("60m", 60),
        Pair("90m", 90)
    )

    val speedOptions = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)

    Scaffold(
        containerColor = CherryBackground,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SETTINGS",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = CherryTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = CherryPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CherryBackground)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 32.dp, top = 8.dp)
        ) {
            // Theme Glow Section
            item {
                SettingsSectionCard(title = "Appearance & Glow", icon = Icons.Rounded.Lightbulb) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Theme Glow Effect",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = CherryTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Enable soft red radial glow behind UI content",
                                style = MaterialTheme.typography.bodyMedium,
                                color = CherryTextSecondary
                            )
                        }
                        Switch(
                            checked = settingsUiState.darkThemeGlow,
                            onCheckedChange = { settingsViewModel.setDarkThemeGlow(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = CherryPrimary,
                                uncheckedThumbColor = CherryTextSecondary,
                                uncheckedTrackColor = CherrySurface
                            )
                        )
                    }
                }
            }

            // Playback Speed Section
            item {
                SettingsSectionCard(title = "Playback Speed", icon = Icons.Rounded.Speed) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Current Speed: ${String.format(java.util.Locale.US, "%.2fx", settingsUiState.playbackSpeed)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = CherryPrimary,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            TextButton(onClick = { playerViewModel.setPlaybackSpeed(1.0f) }) {
                                Text("Reset to 1.0x", color = CherryTextSecondary)
                            }
                        }

                        // Speed Chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(speedOptions) { speed ->
                                val isSelected = kotlin.math.abs(settingsUiState.playbackSpeed - speed) < 0.01f
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) CherryPrimary else CherryBorder,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { playerViewModel.setPlaybackSpeed(speed) },
                                    color = if (isSelected) CherryHighlight else CherrySurface
                                ) {
                                    Text(
                                        text = "${speed}x",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) CherryPrimary else CherryTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Equalizer Presets Section
            item {
                SettingsSectionCard(title = "Equalizer Presets", icon = Icons.Rounded.Equalizer) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Select sound profile preset",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CherryTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Grid of presets
                        val rows = eqPresets.chunked(2)
                        rows.forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowItems.forEach { preset ->
                                    val isSelected = settingsUiState.equalizerPreset == preset
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) CherryHighlight else CherrySurface)
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) CherryPrimary else CherryBorder,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable { settingsViewModel.setEqualizerPreset(preset) }
                                            .padding(vertical = 12.dp, horizontal = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = preset,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) CherryPrimary else CherryTextPrimary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                if (rowItems.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // Sleep Timer Section
            item {
                SettingsSectionCard(title = "Sleep Timer", icon = Icons.Rounded.Timer) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (playerUiState.sleepTimerMinutes != null && playerUiState.sleepTimerRemainingMillis != null) {
                            val remainingMinutes = (playerUiState.sleepTimerRemainingMillis!! / 1000 / 60) + 1
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Timer active: ~${remainingMinutes}m remaining",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = CherryPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                TextButton(onClick = { playerViewModel.setSleepTimer(0) }) {
                                    Text("Cancel", color = CherryPrimaryDark)
                                }
                            }
                        } else {
                            Text(
                                text = "Pause playback automatically after a set duration",
                                style = MaterialTheme.typography.bodyMedium,
                                color = CherryTextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(sleepTimerOptions) { (label, minutes) ->
                                val isSelected = if (minutes == 0) playerUiState.sleepTimerMinutes == null else playerUiState.sleepTimerMinutes == minutes
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) CherryPrimary else CherryBorder,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { playerViewModel.setSleepTimer(minutes) },
                                    color = if (isSelected) CherryHighlight else CherrySurface
                                ) {
                                    Text(
                                        text = label,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) CherryPrimary else CherryTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // About Section
            item {
                SettingsSectionCard(title = "About", icon = Icons.Rounded.Info) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Cherry Music Player // Asta v1.0",
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = CherryPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Futuristic local music experience crafted with Material 3 & Jetpack Compose.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CherryTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CherryBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CherrySurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = CherryPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CherryTextPrimary,
                    fontFamily = FontFamily.Monospace
                )
            }
            HorizontalDivider(color = CherryBorder.copy(alpha = 0.5f))
            content()
        }
    }
}
