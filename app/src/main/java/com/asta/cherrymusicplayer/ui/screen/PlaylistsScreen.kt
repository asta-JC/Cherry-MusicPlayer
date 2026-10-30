package com.asta.cherrymusicplayer.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.asta.cherrymusicplayer.data.entity.PlaylistEntity
import com.asta.cherrymusicplayer.data.entity.SongEntity
import com.asta.cherrymusicplayer.ui.component.CherryIcon
import com.asta.cherrymusicplayer.ui.theme.*
import com.asta.cherrymusicplayer.ui.viewmodel.LibraryViewModel
import com.asta.cherrymusicplayer.ui.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsScreen(
    libraryViewModel: LibraryViewModel,
    playerViewModel: PlayerViewModel,
    onPlaylistClick: (PlaylistEntity?) -> Unit,
    onSettingsClick: () -> Unit = {}
) {
    val uiState by libraryViewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }

    Scaffold(
        containerColor = CherryBackground,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Playlists",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        color = CherryTextPrimary
                    )
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings",
                            tint = CherryPrimary
                        )
                    }
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "New Playlist",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, CherryBorder, RoundedCornerShape(16.dp))
                        .clickable { onPlaylistClick(null) },
                    colors = CardDefaults.cardColors(containerColor = CherrySurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CherryBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            CherryIcon(
                                filled = true,
                                contentDescription = null,
                                size = 28.dp
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Favorites",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = CherryTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Built-in favorite tracks",
                                style = MaterialTheme.typography.bodyMedium,
                                color = CherryTextSecondary
                            )
                        }
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            tint = CherryTextSecondary
                        )
                    }
                }
            }

            if (uiState.playlists.isEmpty()) {
                item {
                    Column(Modifier.fillMaxWidth().padding(vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("A playlist for every mood", style = MaterialTheme.typography.titleMedium)
                        Text("Bring your favorite songs together.", color = CherryTextSecondary)
                        Button(onClick = { showCreateDialog = true }) { Icon(Icons.Rounded.Add, null); Spacer(Modifier.width(8.dp)); Text("Create playlist") }
                    }
                }
            }
            items(uiState.playlists, key = { it.id }) { playlist ->
                var showOptions by remember { mutableStateOf(false) }
                var showRenameDialog by remember { mutableStateOf(false) }
                var showDeleteDialog by remember { mutableStateOf(false) }
                var renameText by remember { mutableStateOf(playlist.name) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, CherryBorder, RoundedCornerShape(16.dp))
                        .clickable { onPlaylistClick(playlist) },
                    colors = CardDefaults.cardColors(containerColor = CherrySurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CherryBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                                contentDescription = null,
                                tint = CherryPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = playlist.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = CherryTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Playlist",
                                style = MaterialTheme.typography.bodyMedium,
                                color = CherryTextSecondary
                            )
                        }

                        Box {
                            IconButton(onClick = { showOptions = true }) {
                                Icon(
                                    imageVector = Icons.Rounded.MoreVert,
                                    contentDescription = "Options",
                                    tint = CherryTextSecondary
                                )
                            }
                            DropdownMenu(
                                expanded = showOptions,
                                onDismissRequest = { showOptions = false },
                                modifier = Modifier.background(CherrySurface)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Rename", color = CherryTextPrimary) },
                                    onClick = {
                                        showOptions = false
                                        showRenameDialog = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                                    onClick = {
                                        showOptions = false
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                }

                if (showDeleteDialog) {
                    AlertDialog(onDismissRequest = { showDeleteDialog = false },
                        title = { Text("Delete playlist?") },
                        text = { Text("Remove “${playlist.name}”? Your music files will stay on this phone.") },
                        confirmButton = { TextButton(onClick = { playerViewModel.deletePlaylist(playlist.id); showDeleteDialog = false }) {
                            Text("Delete", color = MaterialTheme.colorScheme.error)
                        } },
                        dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } })
                }
                if (showRenameDialog) {
                    AlertDialog(
                        onDismissRequest = { showRenameDialog = false },
                        containerColor = CherrySurface,
                        title = { Text("Rename playlist", color = CherryTextPrimary) },
                        text = {
                            OutlinedTextField(
                                value = renameText,
                                onValueChange = { renameText = it },
                                label = { Text("New name") },
                                singleLine = true
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    if (renameText.isNotBlank()) {
                                        playerViewModel.renamePlaylist(playlist.id, renameText.trim())
                                        showRenameDialog = false
                                    }
                                }
                            ) {
                                Text("Save", color = CherryPrimary)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showRenameDialog = false }) {
                                Text("Cancel", color = CherryTextSecondary)
                            }
                        }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = CherrySurface,
            title = { Text("Create playlist", color = CherryTextPrimary) },
            text = {
                OutlinedTextField(
                    value = newPlaylistName,
                    onValueChange = { newPlaylistName = it },
                    label = { Text("Playlist name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newPlaylistName.isNotBlank()) {
                            playerViewModel.createPlaylist(newPlaylistName.trim())
                            newPlaylistName = ""
                            showCreateDialog = false
                        }
                    }
                ) {
                    Text("Create", color = CherryPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = CherryTextSecondary)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    playlistId: Long?,
    playlistName: String,
    libraryViewModel: LibraryViewModel,
    playerViewModel: PlayerViewModel,
    onBackClick: () -> Unit
) {
    val songsFlow = remember(playlistId, playerViewModel) {
        if (playlistId == null) playerViewModel.favoriteSongs else playerViewModel.getSongsForPlaylist(playlistId)
    }
    val songs by songsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val allSongs by libraryViewModel.uiState.collectAsStateWithLifecycle()
    val playerUiState by playerViewModel.uiState.collectAsStateWithLifecycle()

    var showAddSongsDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = CherryBackground,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            TopAppBar(
                title = { Text(playlistName, color = CherryTextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = CherryTextPrimary)
                    }
                },
                actions = {
                    if (playlistId != null) {
                        IconButton(onClick = { showAddSongsDialog = true }) {
                            Icon(Icons.Rounded.Add, contentDescription = "Add Songs", tint = CherryPrimary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CherryBackground)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (songs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(if (playlistId == null) "Your favorites belong here" else "Make this playlist yours", style = MaterialTheme.typography.titleMedium)
                        Text(if (playlistId == null) "Tap the cherry beside a song to save it." else "Add a few songs to get started.", color = CherryTextSecondary)
                        if (playlistId != null) Button(onClick = { showAddSongsDialog = true }) { Text("Add songs") }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(songs, key = { it.id }) { song ->
                        SongItemCard(
                            song = song,
                            isPlaying = song.id == playerUiState.currentSong?.id,
                            onClick = { playerViewModel.playSong(song, songs) },
                            onFavoriteClick = { playerViewModel.toggleFavorite(song) },
                            onLongClick = if (playlistId != null) ({ playerViewModel.removeSongFromPlaylist(playlistId, song.id) }) else null,
                            overflowLabel = "Remove from playlist"
                        )
                    }
                }
            }
        }
    }

    if (showAddSongsDialog && playlistId != null) {
        val allAvailableSongs = allSongs.songs
        AlertDialog(
            onDismissRequest = { showAddSongsDialog = false },
            containerColor = CherrySurface,
            title = { Text("Add songs to playlist", color = CherryTextPrimary) },
            text = {
                LazyColumn(
                    modifier = Modifier.height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(allAvailableSongs, key = { it.id }) { song ->
                        val inPlaylist = songs.any { it.id == song.id }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (inPlaylist) {
                                        playerViewModel.removeSongFromPlaylist(playlistId, song.id)
                                    } else {
                                        playerViewModel.addSongToPlaylist(playlistId, song.id)
                                    }
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(song.title, color = CherryTextPrimary, maxLines = 1)
                                Text(song.artist, color = CherryTextSecondary, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                            }
                            Checkbox(
                                checked = inPlaylist,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        playerViewModel.addSongToPlaylist(playlistId, song.id)
                                    } else {
                                        playerViewModel.removeSongFromPlaylist(playlistId, song.id)
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = CherryPrimary)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddSongsDialog = false }) {
                    Text("Done", color = CherryPrimary)
                }
            }
        )
    }
}
