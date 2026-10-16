package com.asta.cherrymusicplayer.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Screen : NavKey {
    @Serializable
    data object Library : Screen

    @Serializable
    data object Playlists : Screen

    @Serializable
    data class PlaylistDetail(val playlistId: Long?, val playlistName: String) : Screen

    @Serializable
    data class FilteredSongList(
        val title: String,
        val subtitle: String? = null,
        val filterType: String, // "ALBUM", "ARTIST", "PLAYLIST"
        val filterValue: String
    ) : Screen

    @Serializable
    data object NowPlaying : Screen

    @Serializable
    data object Settings : Screen
}
