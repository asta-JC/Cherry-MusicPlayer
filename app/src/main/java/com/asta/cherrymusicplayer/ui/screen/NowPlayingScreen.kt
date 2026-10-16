package com.asta.cherrymusicplayer.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import coil.compose.AsyncImage
import com.asta.cherrymusicplayer.ui.component.CherryIcon
import com.asta.cherrymusicplayer.ui.theme.*
import com.asta.cherrymusicplayer.ui.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    viewModel: PlayerViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val song = uiState.currentSong

    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var showQueueSheet by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "AlbumRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotation"
    )

    Scaffold(
        containerColor = CherryBackground,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "NOW PLAYING",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = CherryTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "Collapse",
                            tint = CherryTextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showQueueSheet = true }) {
                        Icon(
                            imageVector = Icons.Rounded.QueueMusic,
                            contentDescription = "Queue",
                            tint = CherryPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CherryBackground)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (song == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No song playing", color = CherryTextSecondary)
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .shadow(
                            elevation = 24.dp,
                            shape = CircleShape,
                            ambientColor = CherryPrimary,
                            spotColor = CherryPrimaryDark
                        )
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(CherryPrimary.copy(alpha = 0.4f), Color.Transparent)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .clip(CircleShape)
                            .background(CherrySurface)
                            .border(2.dp, CherryBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = song.albumArtUri ?: android.R.drawable.ic_media_play,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .rotate(if (uiState.isPlaying) rotation else 0f),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = song.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = CherryTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = song.artist,
                            style = MaterialTheme.typography.bodyLarge,
                            color = CherryTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = { viewModel.toggleFavorite(song) }
                    ) {
                        CherryIcon(
                            filled = song.isFavorite,
                            size = 28.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Slider(
                        value = if (uiState.duration > 0) uiState.position.toFloat() / uiState.duration.toFloat() else 0f,
                        onValueChange = { newValue ->
                            viewModel.seekTo((newValue * uiState.duration).toLong())
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = CherryPrimary,
                            activeTrackColor = CherryPrimary,
                            inactiveTrackColor = CherrySurface
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(uiState.position),
                            style = MaterialTheme.typography.bodySmall,
                            color = CherryTextSecondary
                        )
                        Text(
                            text = formatTime(uiState.duration),
                            style = MaterialTheme.typography.bodySmall,
                            color = CherryTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.toggleShuffle() }) {
                        Icon(
                            imageVector = Icons.Rounded.Shuffle,
                            contentDescription = "Shuffle",
                            tint = if (uiState.shuffleModeEnabled) CherryPrimary else CherryTextSecondary,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    IconButton(onClick = { viewModel.skipToPrevious() }, modifier = Modifier.size(48.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.SkipPrevious,
                            contentDescription = "Previous",
                            tint = CherryTextPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .shadow(
                                elevation = 16.dp,
                                shape = CircleShape,
                                ambientColor = CherryPrimary,
                                spotColor = CherryPrimaryDark
                            )
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFFFF1744), CherryPrimary)
                                ),
                                shape = CircleShape
                            )
                            .clickable { viewModel.togglePlayPause() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (uiState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    IconButton(onClick = { viewModel.skipToNext() }, modifier = Modifier.size(48.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.SkipNext,
                            contentDescription = "Next",
                            tint = CherryTextPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    IconButton(onClick = { viewModel.toggleRepeatMode() }) {
                        Icon(
                            imageVector = when (uiState.repeatMode) {
                                Player.REPEAT_MODE_ONE -> Icons.Rounded.RepeatOne
                                else -> Icons.Rounded.Repeat
                            },
                            contentDescription = "Repeat",
                            tint = if (uiState.repeatMode != Player.REPEAT_MODE_OFF) CherryPrimary else CherryTextSecondary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showSleepTimerDialog = true }) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Icon(
                                imageVector = Icons.Rounded.Bedtime,
                                contentDescription = "Sleep Timer",
                                tint = if (uiState.sleepTimerMinutes != null) CherryPrimary else CherryTextPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                            if (uiState.sleepTimerMinutes != null) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(CherryPrimary, CircleShape)
                                )
                            }
                        }
                    }

                    var volume by remember { mutableFloatStateOf(uiState.volume) }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.width(180.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.VolumeDown,
                            contentDescription = "Volume",
                            tint = CherryTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                        Slider(
                            value = volume,
                            onValueChange = {
                                volume = it
                                viewModel.setVolume(it)
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = CherryPrimary,
                                activeTrackColor = CherryPrimary,
                                inactiveTrackColor = CherrySurface
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Rounded.VolumeUp,
                            contentDescription = "Volume Up",
                            tint = CherryTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }

    if (showSleepTimerDialog) {
        AlertDialog(
            onDismissRequest = { showSleepTimerDialog = false },
            containerColor = CherrySurface,
            title = { Text("Sleep Timer", color = CherryTextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(15, 30, 45, 60, 90).forEach { minutes ->
                        TextButton(
                            onClick = {
                                viewModel.setSleepTimer(minutes)
                                showSleepTimerDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "$minutes minutes",
                                color = if (uiState.sleepTimerMinutes == minutes) CherryPrimary else CherryTextPrimary
                            )
                        }
                    }
                    TextButton(
                        onClick = {
                            viewModel.setSleepTimer(0)
                            showSleepTimerDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Turn Off Timer", color = CherryPrimary)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSleepTimerDialog = false }) {
                    Text("Close", color = CherryPrimary)
                }
            }
        )
    }

    if (showQueueSheet) {
        ModalBottomSheet(
            onDismissRequest = { showQueueSheet = false },
            containerColor = CherrySurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Playback Queue (${uiState.queue.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = CherryTextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxHeight(0.6f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.queue.size) { index ->
                        val item = uiState.queue[index]
                        val isCurrent = index == uiState.currentIndex
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isCurrent) CherryHighlight else Color.Transparent)
                                .clickable { viewModel.playQueue(uiState.queue, index) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "${index + 1}",
                                color = if (isCurrent) CherryPrimary else CherryTextSecondary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    color = if (isCurrent) CherryPrimary else CherryTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = item.artist,
                                    color = CherryTextSecondary,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
