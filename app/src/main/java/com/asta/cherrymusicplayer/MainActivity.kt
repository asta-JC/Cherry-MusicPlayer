package com.asta.cherrymusicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.asta.cherrymusicplayer.ui.component.MiniPlayer
import com.asta.cherrymusicplayer.ui.navigation.Screen
import com.asta.cherrymusicplayer.ui.screen.LibraryScreen
import com.asta.cherrymusicplayer.ui.screen.NowPlayingScreen
import com.asta.cherrymusicplayer.ui.screen.PlaylistDetailScreen
import com.asta.cherrymusicplayer.ui.screen.PlaylistsScreen
import com.asta.cherrymusicplayer.ui.screen.SettingsScreen
import com.asta.cherrymusicplayer.ui.theme.*
import com.asta.cherrymusicplayer.ui.viewmodel.LibraryViewModel
import com.asta.cherrymusicplayer.ui.viewmodel.PlayerViewModel
import com.asta.cherrymusicplayer.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CherryMusicPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CherryBackground
                ) {
                    val backStack = remember { mutableStateListOf<Any>(Screen.Library) }
                    val libraryViewModel: LibraryViewModel = hiltViewModel()
                    val playerViewModel: PlayerViewModel = hiltViewModel()
                    val settingsViewModel: SettingsViewModel = hiltViewModel()

                    val playerState by playerViewModel.uiState.collectAsState()
                    val settingsState by settingsViewModel.uiState.collectAsState()

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(CherryBackground)
                    ) {
                        if (settingsState.darkThemeGlow) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                CherryPrimary.copy(alpha = 0.18f),
                                                Color.Transparent
                                            ),
                                            radius = 1400f
                                        )
                                    )
                            )
                        }

                        NavDisplay(
                            backStack = backStack,
                            onBack = {
                                if (backStack.size > 1) {
                                    backStack.removeLastOrNull()
                                }
                            },
                            entryProvider = { key ->
                                when (key) {
                                    is Screen.Library -> NavEntry(key) {
                                        Scaffold(
                                            containerColor = Color.Transparent,
                                            bottomBar = {
                                                Column {
                                                    MiniPlayer(
                                                        currentSong = playerState.currentSong,
                                                        isPlaying = playerState.isPlaying,
                                                        onPlayPauseClick = { playerViewModel.togglePlayPause() },
                                                        onNextClick = { playerViewModel.skipToNext() },
                                                        onClick = { backStack.add(Screen.NowPlaying) }
                                                    )
                                                    NavigationBar(
                                                        containerColor = CherrySurface,
                                                        contentColor = CherryPrimary
                                                    ) {
                                                        NavigationBarItem(
                                                            selected = true,
                                                            onClick = { },
                                                            icon = { Icon(Icons.Rounded.LibraryMusic, contentDescription = "Library") },
                                                            label = { Text("Library") },
                                                            colors = NavigationBarItemDefaults.colors(
                                                                selectedIconColor = CherryPrimary,
                                                                unselectedIconColor = CherryTextSecondary,
                                                                selectedTextColor = CherryPrimary,
                                                                unselectedTextColor = CherryTextSecondary,
                                                                indicatorColor = CherryHighlight
                                                            )
                                                        )
                                                        NavigationBarItem(
                                                            selected = false,
                                                            onClick = { backStack.add(Screen.Playlists) },
                                                            icon = { Icon(Icons.AutoMirrored.Rounded.QueueMusic, contentDescription = "Playlists") },
                                                            label = { Text("Playlists") },
                                                            colors = NavigationBarItemDefaults.colors(
                                                                selectedIconColor = CherryPrimary,
                                                                unselectedIconColor = CherryTextSecondary,
                                                                selectedTextColor = CherryPrimary,
                                                                unselectedTextColor = CherryTextSecondary,
                                                                indicatorColor = CherryHighlight
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        ) { paddingValues ->
                                            Box(modifier = Modifier.padding(paddingValues)) {
                                                LibraryScreen(
                                                    viewModel = libraryViewModel,
                                                    onSongClick = { song ->
                                                        playerViewModel.setAllSongsList(libraryViewModel.uiState.value.songs)
                                                        playerViewModel.playSong(song, libraryViewModel.uiState.value.songs)
                                                    },
                                                    onSettingsClick = { backStack.add(Screen.Settings) }
                                                )
                                            }
                                        }
                                    }
                                    is Screen.Playlists -> NavEntry(key) {
                                        Scaffold(
                                            containerColor = Color.Transparent,
                                            bottomBar = {
                                                Column {
                                                    MiniPlayer(
                                                        currentSong = playerState.currentSong,
                                                        isPlaying = playerState.isPlaying,
                                                        onPlayPauseClick = { playerViewModel.togglePlayPause() },
                                                        onNextClick = { playerViewModel.skipToNext() },
                                                        onClick = { backStack.add(Screen.NowPlaying) }
                                                    )
                                                    NavigationBar(
                                                        containerColor = CherrySurface,
                                                        contentColor = CherryPrimary
                                                    ) {
                                                        NavigationBarItem(
                                                            selected = false,
                                                            onClick = {
                                                                if (backStack.size > 1) backStack.removeLastOrNull()
                                                            },
                                                            icon = { Icon(Icons.Rounded.LibraryMusic, contentDescription = "Library") },
                                                            label = { Text("Library") },
                                                            colors = NavigationBarItemDefaults.colors(
                                                                selectedIconColor = CherryPrimary,
                                                                unselectedIconColor = CherryTextSecondary,
                                                                selectedTextColor = CherryPrimary,
                                                                unselectedTextColor = CherryTextSecondary,
                                                                indicatorColor = CherryHighlight
                                                            )
                                                        )
                                                        NavigationBarItem(
                                                            selected = true,
                                                            onClick = { },
                                                            icon = { Icon(Icons.AutoMirrored.Rounded.QueueMusic, contentDescription = "Playlists") },
                                                            label = { Text("Playlists") },
                                                            colors = NavigationBarItemDefaults.colors(
                                                                selectedIconColor = CherryPrimary,
                                                                unselectedIconColor = CherryTextSecondary,
                                                                selectedTextColor = CherryPrimary,
                                                                unselectedTextColor = CherryTextSecondary,
                                                                indicatorColor = CherryHighlight
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        ) { paddingValues ->
                                            Box(modifier = Modifier.padding(paddingValues)) {
                                                PlaylistsScreen(
                                                    libraryViewModel = libraryViewModel,
                                                    playerViewModel = playerViewModel,
                                                    onPlaylistClick = { playlist ->
                                                        backStack.add(
                                                            Screen.PlaylistDetail(
                                                                playlistId = playlist?.id,
                                                                playlistName = playlist?.name ?: "Favorites"
                                                            )
                                                        )
                                                    },
                                                    onSettingsClick = { backStack.add(Screen.Settings) }
                                                )
                                            }
                                        }
                                    }
                                    is Screen.PlaylistDetail -> NavEntry(key) {
                                        PlaylistDetailScreen(
                                            playlistId = key.playlistId,
                                            playlistName = key.playlistName,
                                            libraryViewModel = libraryViewModel,
                                            playerViewModel = playerViewModel,
                                            onBackClick = { backStack.removeLastOrNull() }
                                        )
                                    }
                                    is Screen.NowPlaying -> NavEntry(key) {
                                        NowPlayingScreen(
                                            viewModel = playerViewModel,
                                            onBackClick = { backStack.removeLastOrNull() }
                                        )
                                    }
                                    is Screen.Settings -> NavEntry(key) {
                                        SettingsScreen(
                                            settingsViewModel = settingsViewModel,
                                            playerViewModel = playerViewModel,
                                            onBackClick = { backStack.removeLastOrNull() }
                                        )
                                    }
                                    else -> error("Unknown route: $key")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
