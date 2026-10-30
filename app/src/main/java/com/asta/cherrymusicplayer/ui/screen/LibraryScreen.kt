package com.asta.cherrymusicplayer.ui.screen

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.*
import com.asta.cherrymusicplayer.R
import com.asta.cherrymusicplayer.ui.component.SongArtwork
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.asta.cherrymusicplayer.data.entity.PlaylistEntity
import com.asta.cherrymusicplayer.data.entity.SongEntity
import com.asta.cherrymusicplayer.data.repository.Album
import com.asta.cherrymusicplayer.data.repository.Artist
import com.asta.cherrymusicplayer.ui.component.CherryIcon
import com.asta.cherrymusicplayer.ui.theme.*
import com.asta.cherrymusicplayer.ui.viewmodel.LibraryTab
import com.asta.cherrymusicplayer.ui.viewmodel.LibraryViewModel
import com.asta.cherrymusicplayer.ui.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    currentPlayingSongId: Long? = null,
    onSongClick: (SongEntity) -> Unit = {},
    onAlbumClick: (Album) -> Unit = {},
    onArtistClick: (Artist) -> Unit = {},
    onPlaylistClick: (PlaylistEntity) -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by rememberSaveable { mutableStateOf(viewModel.searchQuery.value) }
    val changeSearch: (String) -> Unit = { searchQuery = it; viewModel.updateSearchQuery(it) }
    val context = LocalContext.current
    var songToHide by remember { mutableStateOf<SongEntity?>(null) }

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.setPermissionGranted(isGranted)
    }

    LaunchedEffect(Unit) {
        val granted = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            permission
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        viewModel.setPermissionGranted(granted)
    }

    var showSortMenu by remember { mutableStateOf(false) }

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
                    text = "Hide song",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Hide \"${song.title}\" from your library? You can restore hidden songs in Settings.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.hideSong(song)
                        songToHide = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CherryPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Hide",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { songToHide = null },
                    border = androidx.compose.foundation.BorderStroke(1.dp, CherryBorder),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Cancel",
                        color = CherryTextSecondary,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        )
    }

    Scaffold(
        containerColor = CherryBackground,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CherryBackground)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Top Bar Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(
                                    if (CherryBackground.luminance() < .5f) R.drawable.cherryapps_logo_full_dark
                                    else R.drawable.cherryapps_logo_full
                                ),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize().padding(2.dp)
                            )
                        }
                        Text(
                            text = "Cherry Music",
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = CherryTextPrimary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = "Settings",
                                tint = CherryPrimary
                            )
                        }
                        Box {
                            IconButton(onClick = { showSortMenu = true }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.Sort,
                                    contentDescription = "Sort",
                                    tint = CherryPrimary
                                )
                            }

                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false },
                                modifier = Modifier.background(CherrySurface)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Sort by Title", color = CherryTextPrimary) },
                                    onClick = {
                                        viewModel.setSortOption(SortOption.TITLE)
                                        showSortMenu = false
                                    },
                                    leadingIcon = {
                                        if (uiState.sortOption == SortOption.TITLE) {
                                            Icon(Icons.Rounded.Check, null, tint = CherryPrimary)
                                        }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Sort by Artist", color = CherryTextPrimary) },
                                    onClick = {
                                        viewModel.setSortOption(SortOption.ARTIST)
                                        showSortMenu = false
                                    },
                                    leadingIcon = {
                                        if (uiState.sortOption == SortOption.ARTIST) {
                                            Icon(Icons.Rounded.Check, null, tint = CherryPrimary)
                                        }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Sort by Date Added", color = CherryTextPrimary) },
                                    onClick = {
                                        viewModel.setSortOption(SortOption.DATE_ADDED)
                                        showSortMenu = false
                                    },
                                    leadingIcon = {
                                        if (uiState.sortOption == SortOption.DATE_ADDED) {
                                            Icon(Icons.Rounded.Check, null, tint = CherryPrimary)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Pill-shaped Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = changeSearch,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    placeholder = {
                        Text(
                            "Search songs, albums, artists...",
                            color = CherryTextSecondary,
                            fontFamily = FontFamily.SansSerif
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search",
                            tint = CherryPrimary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { changeSearch("") }) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Clear",
                                    tint = CherryTextSecondary
                                )
                            }
                        }
                    },
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CherrySurface,
                        unfocusedContainerColor = CherrySurface,
                        disabledContainerColor = CherrySurface,
                        focusedBorderColor = CherryPrimary,
                        unfocusedBorderColor = CherryBorder,
                        focusedTextColor = CherryTextPrimary,
                        unfocusedTextColor = CherryTextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Filter Tabs (Songs / Albums / Artists / Playlists)
                PrimaryScrollableTabRow(
                    selectedTabIndex = uiState.currentTab.ordinal,
                    containerColor = Color.Transparent,
                    contentColor = CherryPrimary,
                    edgePadding = 0.dp,
                    indicator = {},
                    divider = {}
                ) {
                    val tabs = listOf(
                        LibraryTab.SONGS to "Songs",
                        LibraryTab.ALBUMS to "Albums",
                        LibraryTab.ARTISTS to "Artists"
                    )
                    tabs.forEach { (tab, title) ->
                        val selected = uiState.currentTab == tab
                        Tab(
                            selected = selected,
                            onClick = { viewModel.setTab(tab) },
                            text = {
                                Text(
                                    text = title,
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) CherryPrimary else CherryTextSecondary
                                )
                            },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selected) CherrySurface else Color.Transparent)
                                .border(
                                    width = if (selected) 1.5.dp else 1.dp,
                                    color = if (selected) CherryPrimary else CherryBorder,
                                    shape = RoundedCornerShape(20.dp)
                                )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CherryBackground)
        ) {
            if (!uiState.hasPermission) {
                // Permission Request State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FolderSpecial,
                        contentDescription = "Permission",
                        tint = CherryPrimary,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Storage Permission Required",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.SansSerif,
                        color = CherryTextPrimary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Cherry Player needs access to your local audio library to scan and play your favorite music files.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CherryTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { permissionLauncher.launch(permission) },
                        colors = ButtonDefaults.buttonColors(containerColor = CherryPrimary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .defaultMinSize(minWidth = 180.dp)
                    ) {
                        Text(
                            text = "Allow music access",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            } else if (uiState.isLoading) {
                // Loading State
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CherryPrimary)
                }
            } else {
                val noResults = uiState.searchQuery.isNotBlank() && when (uiState.currentTab) {
                    LibraryTab.SONGS -> uiState.songs.isEmpty()
                    LibraryTab.ALBUMS -> uiState.albums.isEmpty()
                    LibraryTab.ARTISTS -> uiState.artists.isEmpty()
                }
                if (noResults) {
                    EmptyStateView("No matches", "Try a different song, artist, or album.", "Clear search") {
                        changeSearch("")
                    }
                } else when (uiState.currentTab) {
                    LibraryTab.SONGS -> {
                        if (uiState.songs.isEmpty()) {
                            EmptyStateView("Your music starts here", "Scan your device for mp3, m4a, flac, wav, or ogg files.") {
                                viewModel.scanMedia()
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val activeSongId = currentPlayingSongId ?: uiState.currentPlayingSongId
                                items(uiState.songs, key = { it.id }) { song ->
                                    val isPlaying = song.id == activeSongId
                                    SongItemCard(
                                        song = song,
                                        isPlaying = isPlaying,
                                        onClick = {
                                            viewModel.setCurrentPlayingSong(song.id)
                                            onSongClick(song)
                                        },
                                        onLongClick = {
                                            songToHide = song
                                        },
                                        onFavoriteClick = { viewModel.toggleFavorite(song) }
                                    )
                                }
                            }
                        }
                    }
                    LibraryTab.ALBUMS -> {
                        if (uiState.albums.isEmpty()) {
                            EmptyStateView("No albums yet", "Albums will appear after scanning music files.") {
                                viewModel.scanMedia()
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(uiState.albums, key = { it.id }) { album ->
                                    AlbumItemCard(
                                        album = album,
                                        onClick = { onAlbumClick(album) }
                                    )
                                }
                            }
                        }
                    }
                    LibraryTab.ARTISTS -> {
                        if (uiState.artists.isEmpty()) {
                            EmptyStateView("No artists yet", "Artists will appear after scanning music files.") {
                                viewModel.scanMedia()
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(uiState.artists, key = { it.name }) { artist ->
                                    ArtistItemCard(
                                        artist = artist,
                                        onClick = { onArtistClick(artist) }
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongItemCard(
    song: SongEntity,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    onFavoriteClick: () -> Unit,
    overflowLabel: String = "Hide song"
) {
    var showOptions by remember { mutableStateOf(false) }
    val durationFormatted = remember(song.duration) {
        val minutes = (song.duration / 1000) / 60
        val seconds = (song.duration / 1000) % 60
        String.format("%d:%02d", minutes, seconds)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (isPlaying) 1.5.dp else 1.dp,
                color = if (isPlaying) CherryPrimary else CherryBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .semantics { selected = isPlaying }
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) CherryHighlight else CherrySurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                SongArtwork(song.albumArtUri, Modifier.fillMaxSize())

                if (isPlaying) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(CherryPrimary.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                            contentDescription = "Current song",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1.5f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isPlaying) CherryPrimary else CherryTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${song.artist} · $durationFormatted",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CherryTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                IconButton(onClick = onFavoriteClick, modifier = Modifier.semantics { selected = song.isFavorite }) {
                    CherryIcon(
                        filled = song.isFavorite,
                        size = 24.dp
                    )
                }
                if (onLongClick != null) {
                    Box {
                        IconButton(onClick = { showOptions = true }) { Icon(Icons.Rounded.MoreVert, "More options for ${song.title}") }
                        DropdownMenu(expanded = showOptions, onDismissRequest = { showOptions = false }) {
                            DropdownMenuItem(text = { Text(overflowLabel) }, leadingIcon = { Icon(if (overflowLabel == "Hide song") Icons.Rounded.VisibilityOff else Icons.Rounded.RemoveCircleOutline, null) },
                                onClick = { showOptions = false; onLongClick() })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlbumItemCard(
    album: Album,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, CherryBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = CherrySurface)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                SongArtwork(album.artUri, Modifier.fillMaxSize())
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = album.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = CherryTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${album.artist} (${album.songCount} songs)",
                style = MaterialTheme.typography.bodyMedium,
                color = CherryTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ArtistItemCard(
    artist: Artist,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, CherryBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = CherrySurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(CherryBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    tint = CherryPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = artist.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = CherryTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${artist.albumCount} albums • ${artist.songCount} songs",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CherryTextSecondary
                )
            }
        }
    }
}

@Composable
fun PlaylistItemCard(
    playlist: PlaylistEntity,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, CherryBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
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
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CherryBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                    contentDescription = null,
                    tint = CherryPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = CherryTextPrimary
                )
                Text(
                    text = "Playlist",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CherryTextSecondary
                )
            }
        }
    }
}

@Composable
fun EmptyStateView(title: String, subtitle: String, actionLabel: String = "Scan for music", onActionClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.MusicOff,
            contentDescription = null,
            tint = CherryPrimary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontFamily = FontFamily.SansSerif,
            color = CherryTextPrimary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = CherryTextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onActionClick,
            colors = ButtonDefaults.buttonColors(containerColor = CherryPrimary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = actionLabel,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0F)
@Composable
fun SongItemPreview() {
    CherryMusicPlayerTheme {
        SongItemCard(
            song = SongEntity(
                id = 1,
                title = "Cherry Blossom Night",
                artist = "Neon Dreamer",
                album = "Cyberpunk Melodies",
                duration = 215000,
                data = "",
                isFavorite = true
            ),
            isPlaying = true,
            onClick = {},
            onFavoriteClick = {}
        )
    }
}
