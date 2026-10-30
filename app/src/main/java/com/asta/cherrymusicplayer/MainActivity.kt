package com.asta.cherrymusicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.graphics.luminance
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
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
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()
            CherryMusicPlayerTheme(themeMode = settingsState.themeMode) {
                val view = LocalView.current
                val lightBars = MaterialTheme.colorScheme.background.luminance() > .5f
                SideEffect {
                    WindowCompat.getInsetsController(window, view).apply {
                        isAppearanceLightStatusBars = lightBars
                        isAppearanceLightNavigationBars = lightBars
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CherryBackground
                ) {
                    val backStack = rememberNavBackStack(Screen.Library)
                    val libraryViewModel: LibraryViewModel = hiltViewModel()
                    val playerViewModel: PlayerViewModel = hiltViewModel()

                    val playerState by playerViewModel.uiState.collectAsStateWithLifecycle()

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(CherryBackground)
                    ) {
                        fun enterFromBottom(): ContentTransform =
                            (slideInVertically(tween(250), initialOffsetY = { it }) + fadeIn(tween(200)))
                                .togetherWith(fadeOut(tween(200)))

                        fun popExitToBottom(): ContentTransform =
                            fadeIn(tween(200)).togetherWith(slideOutVertically(tween(250), targetOffsetY = { it }) + fadeOut(tween(200)))

                        val defaultForwardEnter = slideInHorizontally(
                            animationSpec = tween(200, easing = FastOutSlowInEasing),
                            initialOffsetX = { fullWidth -> (fullWidth * 0.15f).toInt() }
                        ) + fadeIn(animationSpec = tween(200))

                        val defaultForwardExit = slideOutHorizontally(
                            animationSpec = tween(200, easing = FastOutSlowInEasing),
                            targetOffsetX = { fullWidth -> -(fullWidth * 0.15f).toInt() }
                        ) + fadeOut(animationSpec = tween(200))

                        val defaultPopEnter = slideInHorizontally(
                            animationSpec = tween(200, easing = FastOutSlowInEasing),
                            initialOffsetX = { fullWidth -> -(fullWidth * 0.15f).toInt() }
                        ) + fadeIn(animationSpec = tween(200))

                        val defaultPopExit = slideOutHorizontally(
                            animationSpec = tween(200, easing = FastOutSlowInEasing),
                            targetOffsetX = { fullWidth -> (fullWidth * 0.15f).toInt() }
                        ) + fadeOut(animationSpec = tween(200))

                        NavDisplay(
                            backStack = backStack,
                            entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
                            onBack = {
                                if (backStack.size > 1) {
                                    backStack.removeLastOrNull()
                                }
                            },
                            transitionSpec = {
                                if (isNowPlayingScene(targetState)) {
                                    enterFromBottom()
                                } else if (isNowPlayingScene(initialState)) {
                                    popExitToBottom()
                                } else {
                                    defaultForwardEnter.togetherWith(defaultForwardExit)
                                }
                            },
                            popTransitionSpec = {
                                if (isNowPlayingScene(initialState)) {
                                    popExitToBottom()
                                } else if (isNowPlayingScene(targetState)) {
                                    enterFromBottom()
                                } else {
                                    defaultPopEnter.togetherWith(defaultPopExit)
                                }
                            },
                            predictivePopTransitionSpec = { _ ->
                                if (isNowPlayingScene(initialState)) {
                                    popExitToBottom()
                                } else {
                                    defaultPopEnter.togetherWith(defaultPopExit)
                                }
                            },
                            entryProvider = { key ->
                                when (key) {
                                    is Screen.Library -> NavEntry(key = key) {
                                        Scaffold(
                                            containerColor = Color.Transparent,
                                            bottomBar = {
                                                Column {
                                                    MiniPlayer(
                                                        currentSong = playerState.currentSong,
                                                        isPlaying = playerState.isPlaying,
                                                        viewModel = playerViewModel,
                                                        onPlayPauseClick = { playerViewModel.togglePlayPause() },
                                                        onNextClick = { playerViewModel.skipToNext() },
                                                        onClick = { if (backStack.lastOrNull() != Screen.NowPlaying) backStack.add(Screen.NowPlaying) }
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
                                                    currentPlayingSongId = playerState.currentSong?.id,
                                                    onSongClick = { song ->
                                                        playerViewModel.setAllSongsList(libraryViewModel.uiState.value.songs)
                                                        playerViewModel.playSong(song, libraryViewModel.uiState.value.songs)
                                                    },
                                                    onAlbumClick = { album ->
                                                        backStack.add(
                                                            Screen.FilteredSongList(
                                                                title = album.title,
                                                                subtitle = "Album • ${album.artist} (${album.songCount} songs)",
                                                                filterType = "ALBUM",
                                                                filterValue = album.title
                                                            )
                                                        )
                                                    },
                                                    onArtistClick = { artist ->
                                                        backStack.add(
                                                            Screen.FilteredSongList(
                                                                title = artist.name,
                                                                subtitle = "Artist • ${artist.albumCount} albums • ${artist.songCount} songs",
                                                                filterType = "ARTIST",
                                                                filterValue = artist.name
                                                            )
                                                        )
                                                    },
                                                    onPlaylistClick = { playlist ->
                                                        backStack.add(
                                                            Screen.PlaylistDetail(
                                                                playlistId = playlist.id,
                                                                playlistName = playlist.name
                                                            )
                                                        )
                                                    },
                                                    onSettingsClick = { backStack.add(Screen.Settings) }
                                                )
                                            }
                                        }
                                    }
                                    is Screen.Playlists -> NavEntry(key = key) {
                                        Scaffold(
                                            containerColor = Color.Transparent,
                                            bottomBar = {
                                                Column {
                                                    MiniPlayer(
                                                        currentSong = playerState.currentSong,
                                                        isPlaying = playerState.isPlaying,
                                                        viewModel = playerViewModel,
                                                        onPlayPauseClick = { playerViewModel.togglePlayPause() },
                                                        onNextClick = { playerViewModel.skipToNext() },
                                                        onClick = { if (backStack.lastOrNull() != Screen.NowPlaying) backStack.add(Screen.NowPlaying) }
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
                                    is Screen.FilteredSongList -> NavEntry(key = key) {
                                        Scaffold(
                                            containerColor = Color.Transparent,
                                            bottomBar = {
                                                MiniPlayer(
                                                    currentSong = playerState.currentSong,
                                                    isPlaying = playerState.isPlaying,
                                                    viewModel = playerViewModel,
                                                    onPlayPauseClick = { playerViewModel.togglePlayPause() },
                                                    onNextClick = { playerViewModel.skipToNext() },
                                                    onClick = { if (backStack.lastOrNull() != Screen.NowPlaying) backStack.add(Screen.NowPlaying) }
                                                )
                                            }
                                        ) { paddingValues ->
                                            Box(modifier = Modifier.padding(paddingValues)) {
                                                com.asta.cherrymusicplayer.ui.screen.FilteredSongListScreen(
                                                    title = key.title,
                                                    subtitle = key.subtitle,
                                                    filterType = key.filterType,
                                                    filterValue = key.filterValue,
                                                    libraryViewModel = libraryViewModel,
                                                    playerViewModel = playerViewModel,
                                                    onBackClick = { backStack.removeLastOrNull() }
                                                )
                                            }
                                        }
                                    }
                                    is Screen.PlaylistDetail -> NavEntry(key = key) {
                                        Scaffold(
                                            containerColor = Color.Transparent,
                                            bottomBar = {
                                                MiniPlayer(
                                                    currentSong = playerState.currentSong,
                                                    isPlaying = playerState.isPlaying,
                                                    viewModel = playerViewModel,
                                                    onPlayPauseClick = { playerViewModel.togglePlayPause() },
                                                    onNextClick = { playerViewModel.skipToNext() },
                                                    onClick = { if (backStack.lastOrNull() != Screen.NowPlaying) backStack.add(Screen.NowPlaying) }
                                                )
                                            }
                                        ) { paddingValues ->
                                            Box(modifier = Modifier.padding(paddingValues)) {
                                                PlaylistDetailScreen(
                                                    playlistId = key.playlistId,
                                                    playlistName = key.playlistName,
                                                    libraryViewModel = libraryViewModel,
                                                    playerViewModel = playerViewModel,
                                                    onBackClick = { backStack.removeLastOrNull() }
                                                )
                                            }
                                        }
                                    }
                                    is Screen.NowPlaying -> NavEntry(
                                        key = key,
                                        metadata = NavDisplay.transitionSpec {
                                            enterFromBottom()
                                        } + NavDisplay.popTransitionSpec {
                                            popExitToBottom()
                                        } + NavDisplay.predictivePopTransitionSpec {
                                            popExitToBottom()
                                        }
                                    ) {
                                        NowPlayingScreen(
                                            viewModel = playerViewModel,
                                            onBackClick = {
                                                if (backStack.size > 1) {
                                                    backStack.removeLastOrNull()
                                                }
                                            }
                                        )
                                    }
                                    is Screen.Settings -> NavEntry(key = key) {
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

private fun isNowPlayingKey(key: Any?): Boolean {
    if (key == null) return false
    val str = key.toString()
    return key is Screen.NowPlaying || str == "NowPlaying" || str.endsWith("NowPlaying") || str.contains("NowPlaying")
}

private fun isNowPlayingScene(scene: Scene<*>): Boolean {
    if (isNowPlayingKey(scene.key)) return true
    return scene.entries.any { isNowPlayingKey(it.contentKey) }
}

