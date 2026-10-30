package com.asta.cherrymusicplayer.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asta.cherrymusicplayer.data.entity.SongEntity
import com.asta.cherrymusicplayer.ui.viewmodel.PlayerViewModel

@Composable
fun MiniPlayer(currentSong: SongEntity?, isPlaying: Boolean, viewModel: PlayerViewModel,
    onPlayPauseClick: () -> Unit, onNextClick: () -> Unit, onClick: () -> Unit, modifier: Modifier = Modifier) {
    if (currentSong == null) return
    Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp).clickable(onClick = onClick)) {
        Column {
            Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                SongArtwork(currentSong.albumArtUri, Modifier.size(48.dp))
                Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                    Text(currentSong.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(currentSong.artist, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                FilledIconButton(onClick = onPlayPauseClick, modifier = Modifier.size(48.dp)) {
                    Icon(if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, if (isPlaying) "Pause" else "Play")
                }
                IconButton(onClick = onNextClick) { Icon(Icons.Rounded.SkipNext, "Next") }
            }
            MiniProgress(viewModel)
        }
    }
}

@Composable
private fun MiniProgress(viewModel: PlayerViewModel) {
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    LinearProgressIndicator(progress = { progress.fraction }, modifier = Modifier.fillMaxWidth().height(2.dp), drawStopIndicator = {})
}
