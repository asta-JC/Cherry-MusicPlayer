package com.asta.cherrymusicplayer.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import com.asta.cherrymusicplayer.ui.component.*
import com.asta.cherrymusicplayer.ui.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(viewModel: PlayerViewModel, onBackClick: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showQueue by rememberSaveable { mutableStateOf(false) }
    var showTimer by rememberSaveable { mutableStateOf(false) }
    val song = state.currentSong

    Scaffold(topBar = {
        TopAppBar(title = { Text("Now playing", style = MaterialTheme.typography.titleMedium) },
            navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Rounded.KeyboardArrowDown, "Collapse player") } },
            actions = { IconButton(onClick = { showQueue = true }) { Icon(Icons.AutoMirrored.Rounded.QueueMusic, "Queue") } })
    }) { padding ->
        if (song == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("Choose a song from your library") }
        } else {
            BoxWithConstraints(Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp)) {
                val landscape = maxWidth > maxHeight
                val artwork: @Composable () -> Unit = {
                    SongArtwork(song.albumArtUri, Modifier.size(if (landscape) minOf(maxHeight - 24.dp, 300.dp).coerceAtLeast(100.dp) else minOf(maxWidth, maxHeight * .34f, 280.dp)))
                }
                val controls: @Composable () -> Unit = {
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(song.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(4.dp))
                                Text(song.artist, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyLarge)
                            }
                            IconButton(onClick = { viewModel.toggleFavorite(song) }, modifier = Modifier.semantics { selected = song.isFavorite }) {
                                CherryIcon(song.isFavorite, size = 28.dp)
                            }
                        }
                        PlayerSeekBar(viewModel, song.id)
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                            IconToggleButton(checked = state.shuffleModeEnabled, onCheckedChange = { viewModel.toggleShuffle() }) {
                                Icon(Icons.Rounded.Shuffle, "Shuffle")
                            }
                            IconButton(onClick = viewModel::skipToPrevious) { Icon(Icons.Rounded.SkipPrevious, "Previous", Modifier.size(32.dp)) }
                            FilledIconButton(onClick = viewModel::togglePlayPause, modifier = Modifier.size(72.dp)) {
                                Icon(if (state.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                    if (state.isPlaying) "Pause" else "Play", Modifier.size(36.dp))
                            }
                            IconButton(onClick = viewModel::skipToNext) { Icon(Icons.Rounded.SkipNext, "Next", Modifier.size(32.dp)) }
                            IconToggleButton(checked = state.repeatMode != Player.REPEAT_MODE_OFF, onCheckedChange = { viewModel.toggleRepeatMode() }) {
                                Icon(if (state.repeatMode == Player.REPEAT_MODE_ONE) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat,
                                    when (state.repeatMode) { Player.REPEAT_MODE_ONE -> "Repeat one"; Player.REPEAT_MODE_ALL -> "Repeat all"; else -> "Repeat off" })
                            }
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            TextButton(onClick = { showTimer = true }) {
                                SleepyCherryIcon(state.sleepTimerMinutes != null, size = 20.dp)
                                Spacer(Modifier.width(8.dp))
                                Text(state.sleepTimerRemainingMillis?.let { "Timer · ${formatTime(it)}" } ?: "Sleep timer")
                            }
                            TextButton(onClick = { showQueue = true }) { Icon(Icons.AutoMirrored.Rounded.QueueMusic, null); Spacer(Modifier.width(8.dp)); Text("Queue") }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Rounded.VolumeUp, "Volume", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Slider(value = state.volume, onValueChange = viewModel::setVolume,
                                modifier = Modifier.weight(1f).semantics { contentDescription = "Playback volume" })
                        }
                    }
                }
                if (landscape) {
                    Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Box(Modifier.weight(.9f), contentAlignment = Alignment.Center) { artwork() }
                        Column(Modifier.weight(1.1f).verticalScroll(rememberScrollState()).padding(vertical = 12.dp)) { controls() }
                    }
                } else {
                    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        artwork()
                        controls()
                    }
                }
            }
        }
    }
    if (showTimer) {
        AlertDialog(onDismissRequest = { showTimer = false }, title = { Text("Sleep timer") }, text = {
            Column {
                Text("Pause the music when it’s time to rest.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                listOf(0, 5, 10, 15, 30, 60).forEach { minutes ->
                    TextButton(onClick = { viewModel.setSleepTimer(minutes); showTimer = false }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (minutes == 0) "Off" else "$minutes minutes")
                    }
                }
            }
        }, confirmButton = { TextButton(onClick = { showTimer = false }) { Text("Close") } })
    }
    if (showQueue) {
        ModalBottomSheet(onDismissRequest = { showQueue = false }) {
            Text("Up next · ${state.queue.size} songs", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp))
            LazyColumn(Modifier.fillMaxWidth().fillMaxHeight(.7f), contentPadding = PaddingValues(bottom = 24.dp)) {
                itemsIndexed(state.queue, key = { index, song -> "${song.id}:$index" }) { index, item ->
                    ListItem(headlineContent = { Text(item.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        supportingContent = { Text(item.artist, maxLines = 1) },
                        leadingContent = { SongArtwork(item.albumArtUri, Modifier.size(48.dp)) },
                        trailingContent = { if (index == state.currentIndex) Icon(Icons.Rounded.GraphicEq, "Current song") },
                        colors = ListItemDefaults.colors(containerColor = if (index == state.currentIndex) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface),
                        modifier = Modifier.clickable { viewModel.playQueue(state.queue, index) }.semantics { selected = index == state.currentIndex })
                }
            }
        }
    }
}

@Composable
private fun PlayerSeekBar(viewModel: PlayerViewModel, songId: Long) {
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val gesture = remember(songId) { SeekGesture() }
    Column {
        Slider(value = gesture.fraction ?: progress.fraction, enabled = progress.duration > 0,
            onValueChange = gesture::dragTo,
            onValueChangeFinished = {
                gesture.release(progress.duration, viewModel::seekTo)
            }, modifier = Modifier.semantics { contentDescription = "Track position" })
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(formatTime(gesture.fraction?.let { (it * progress.duration).toLong() } ?: progress.position), style = MaterialTheme.typography.labelMedium)
            Text(formatTime(progress.duration), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun formatTime(millis: Long): String = "%d:%02d".format(millis.coerceAtLeast(0) / 60000, millis.coerceAtLeast(0) / 1000 % 60)
