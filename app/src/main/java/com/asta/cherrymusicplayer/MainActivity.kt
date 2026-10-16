package com.asta.cherrymusicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.hilt.navigation.compose.hiltViewModel
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

                        val exitDuration = 1000
                        val enterDuration = 600

                        val nowPlayingEnterTransition = slideInVertically(
                            animationSpec = tween(enterDuration, easing = FastOutSlowInEasing),
                            initialOffsetY = { fullHeight -> fullHeight }
                        ) + scaleIn(
                            animationSpec = tween(enterDuration, easing = FastOutSlowInEasing),
                            initialScale = 0.80f,
                            transformOrigin = TransformOrigin(0.5f, 1f)
                        ) + fadeIn(
                            animationSpec = tween(enterDuration / 2)
                        )

                        val underlyingExitTransition = scaleOut(
                            animationSpec = tween(enterDuration, easing = FastOutSlowInEasing),
                            targetScale = 0.94f
                        ) + fadeOut(
                            animationSpec = tween(enterDuration / 2)
                        )

                        val underlyingEnterTransition = scaleIn(
                            animationSpec = tween(exitDuration, easing = FastOutSlowInEasing),
                            initialScale = 0.94f
                        ) + fadeIn(
                            animationSpec = tween(exitDuration / 2)
                        )

                        val nowPlayingExitTransition = slideOutVertically(
                            animationSpec = tween(exitDuration, easing = FastOutSlowInEasing),
                            targetOffsetY = { fullHeight -> fullHeight }
                        ) + scaleOut(
                            animationSpec = tween(exitDuration, easing = FastOutSlowInEasing),
                            targetScale = 0.80f,
                            transformOrigin = TransformOrigin(0.5f, 1f)
                        ) + fadeOut(
                            animationSpec = tween(350, delayMillis = exitDuration - 350)
                        )

                        fun enterFromBottom(): ContentTransform =
                            nowPlayingEnterTransition.togetherWith(underlyingExitTransition).apply {
                                targetContentZIndex = 1f
                            }

                        fun popExitToBottom(): ContentTransform =
                            underlyingEnterTransition.togetherWith(nowPlayingExitTransition).apply {
                                targetContentZIndex = -1f
                            }

                        val defaultForwardEnter = slideInHorizontally(
                            animationSpec = tween(320, easing = FastOutSlowInEasing),
                            initialOffsetX = { fullWidth -> (fullWidth * 0.15f).toInt() }
                        ) + fadeIn(animationSpec = tween(280))

                        val defaultForwardExit = slideOutHorizontally(
                            animationSpec = tween(320, easing = FastOutSlowInEasing),
                            targetOffsetX = { fullWidth -> -(fullWidth * 0.15f).toInt() }
                        ) + fadeOut(animationSpec = tween(220))

                        val defaultPopEnter = slideInHorizontally(
                            animationSpec = tween(320, easing = FastOutSlowInEasing),
                            initialOffsetX = { fullWidth -> -(fullWidth * 0.15f).toInt() }
                        ) + fadeIn(animationSpec = tween(280))

                        val defaultPopExit = slideOutHorizontally(
                            animationSpec = tween(320, easing = FastOutSlowInEasing),
                            targetOffsetX = { fullWidth -> (fullWidth * 0.15f).toInt() }
                        ) + fadeOut(animationSpec = tween(220))

                        NavDisplay(
                            backStack = backStack,
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
                                                        position = playerState.position,
                                                        duration = playerState.duration,
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
                                                        position = playerState.position,
                                                        duration = playerState.duration,
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
                                    is Screen.FilteredSongList -> NavEntry(key = key) {
                                        Scaffold(
                                            containerColor = Color.Transparent,
                                            bottomBar = {
                                                MiniPlayer(
                                                    currentSong = playerState.currentSong,
                                                    isPlaying = playerState.isPlaying,
                                                    position = playerState.position,
                                                    duration = playerState.duration,
                                                    onPlayPauseClick = { playerViewModel.togglePlayPause() },
                                                    onNextClick = { playerViewModel.skipToNext() },
                                                    onClick = { backStack.add(Screen.NowPlaying) }
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
                                                    position = playerState.position,
                                                    duration = playerState.duration,
                                                    onPlayPauseClick = { playerViewModel.togglePlayPause() },
                                                    onNextClick = { playerViewModel.skipToNext() },
                                                    onClick = { backStack.add(Screen.NowPlaying) }
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

