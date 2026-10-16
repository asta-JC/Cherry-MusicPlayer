package com.asta.cherrymusicplayer.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.asta.cherrymusicplayer.ui.theme.*
import com.asta.cherrymusicplayer.ui.viewmodel.LibraryViewModel
import com.asta.cherrymusicplayer.ui.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilteredSongListScreen(
    title: String,
    subtitle: String? = null,
    filterType: String, // "ALBUM" or "ARTIST"
    filterValue: String,
    libraryViewModel: LibraryViewModel,
    playerViewModel: PlayerViewModel,
    onBackClick: () -> Unit
) {
    val uiState by libraryViewModel.uiState.collectAsState()
    val playerUiState by playerViewModel.uiState.collectAsState()

    // Filter songs according to the filterType and filterValue
    val matchingSongs = remember(uiState.songs, filterType, filterValue) {
        when (filterType) {
            "ALBUM" -> uiState.songs.filter { it.album.equals(filterValue, ignoreCase = true) }
            "ARTIST" -> uiState.songs.filter { it.artist.equals(filterValue, ignoreCase = true) }
            else -> uiState.songs
        }
    }

    var songToHide by remember { mutableStateOf<com.asta.cherrymusicplayer.data.entity.SongEntity?>(null) }

    if (songToHide != null) {
        val song = songToHide!!
        AlertDialog(
            onDismissRequest = { songToHide = null },
            containerColor = CherrySurface,
            titleContentColor = CherryTextPrimary,
            textContentColor = CherryTextSecondary,
            icon = {
                Icon(
                    imageVector = Icons.Rounded.VisibilityOff,
                    contentDescription = null,
                    tint = CherryPrimary,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = "Hide Song",
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Hide \"${song.title}\" from your library? You will never see it here again.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        libraryViewModel.hideSong(song)
                        songToHide = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CherryPrimary),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Hide",
                        color = androidx.compose.ui.graphics.Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { songToHide = null },
                    border = androidx.compose.foundation.BorderStroke(1.dp, CherryBorder),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Cancel",
                        color = CherryTextSecondary,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            }
        )
    }

    Scaffold(
        containerColor = CherryBackground,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = title,
                            color = CherryTextPrimary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1
                        )
                        if (!subtitle.isNullOrEmpty()) {
                            Text(
                                text = subtitle,
                                color = CherryTextSecondary,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = CherryTextPrimary
                        )
                    }
                },
                actions = {
                    if (matchingSongs.isNotEmpty()) {
                        IconButton(onClick = {
                            playerViewModel.playQueue(matchingSongs, 0)
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.PlayCircle,
                                contentDescription = "Play All",
                                tint = CherryPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CherryBackground)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(CherryBackground)
        ) {
            if (matchingSongs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No songs found in this $filterType",
                        color = CherryTextSecondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(matchingSongs, key = { it.id }) { song ->
                        val isPlaying = song.id == playerUiState.currentSong?.id
                        SongItemCard(
                            song = song,
                            isPlaying = isPlaying,
                            onClick = {
                                playerViewModel.playSong(song, matchingSongs)
                            },
                            onLongClick = {
                                songToHide = song
                            },
                            onFavoriteClick = {
                                playerViewModel.toggleFavorite(song)
                            }
                        )
                    }
                }
            }
        }
    }
}
