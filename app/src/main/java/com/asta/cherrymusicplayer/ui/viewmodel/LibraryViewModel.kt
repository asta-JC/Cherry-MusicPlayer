package com.asta.cherrymusicplayer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asta.cherrymusicplayer.data.entity.PlaylistEntity
import com.asta.cherrymusicplayer.data.entity.SongEntity
import com.asta.cherrymusicplayer.data.repository.Album
import com.asta.cherrymusicplayer.data.repository.Artist
import com.asta.cherrymusicplayer.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class LibraryTab {
    SONGS, ALBUMS, ARTISTS, PLAYLISTS
}

enum class SortOption {
    TITLE, ARTIST, DATE_ADDED
}

data class LibraryUiState(
    val songs: List<SongEntity> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val playlists: List<PlaylistEntity> = emptyList(),
    val searchQuery: String = "",
    val currentTab: LibraryTab = LibraryTab.SONGS,
    val sortOption: SortOption = SortOption.TITLE,
    val isLoading: Boolean = false,
    val hasPermission: Boolean = false,
    val currentPlayingSongId: Long? = null
)

data class LibraryRawData(
    val songs: List<SongEntity> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val playlists: List<PlaylistEntity> = emptyList()
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _currentTab = MutableStateFlow(LibraryTab.SONGS)
    private val _sortOption = MutableStateFlow(SortOption.TITLE)
    private val _hasPermission = MutableStateFlow(false)
    private val _isLoading = MutableStateFlow(false)
    private val _currentPlayingSongId = MutableStateFlow<Long?>(null)

    private val rawDataFlow = combine(
        musicRepository.allSongs,
        musicRepository.getAlbumsFlow(),
        musicRepository.getArtistsFlow(),
        musicRepository.allPlaylists
    ) { songs, albums, artists, playlists ->
        LibraryRawData(songs, albums, artists, playlists)
    }

    private val filterStateFlow = combine(
        rawDataFlow,
        _searchQuery,
        _currentTab,
        _sortOption
    ) { rawData, query, tab, sort ->
        Quadruple(rawData, query, tab, sort)
    }

    private val extraStateFlow = combine(
        _hasPermission,
        _isLoading,
        _currentPlayingSongId
    ) { permission, loading, playingId ->
        Triple(permission, loading, playingId)
    }

    val uiState: StateFlow<LibraryUiState> = combine(
        filterStateFlow,
        extraStateFlow
    ) { filter, extra ->
        val rawData = filter.first
        val query = filter.second
        val tab = filter.third
        val sort = filter.fourth

        val permission = extra.first
        val loading = extra.second
        val playingId = extra.third

        val filteredSongs = rawData.songs.filter { song ->
            query.isBlank() ||
                song.title.contains(query, ignoreCase = true) ||
                song.artist.contains(query, ignoreCase = true) ||
                song.album.contains(query, ignoreCase = true)
        }.let { list ->
            when (sort) {
                SortOption.TITLE -> list.sortedBy { it.title.lowercase() }
                SortOption.ARTIST -> list.sortedBy { it.artist.lowercase() }
                SortOption.DATE_ADDED -> list.sortedByDescending { it.dateAdded }
            }
        }

        val filteredAlbums = rawData.albums.filter { album ->
            query.isBlank() ||
                album.title.contains(query, ignoreCase = true) ||
                album.artist.contains(query, ignoreCase = true)
        }.sortedBy { it.title.lowercase() }

        val filteredArtists = rawData.artists.filter { artist ->
            query.isBlank() || artist.name.contains(query, ignoreCase = true)
        }.sortedBy { artist -> artist.name.lowercase() }

        LibraryUiState(
            songs = filteredSongs,
            albums = filteredAlbums,
            artists = filteredArtists,
            playlists = rawData.playlists,
            searchQuery = query,
            currentTab = tab,
            sortOption = sort,
            isLoading = loading,
            hasPermission = permission,
            currentPlayingSongId = playingId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LibraryUiState(isLoading = true)
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setTab(tab: LibraryTab) {
        _currentTab.value = tab
    }

    fun setSortOption(sort: SortOption) {
        _sortOption.value = sort
    }

    fun setPermissionGranted(granted: Boolean) {
        _hasPermission.value = granted
        if (granted) {
            scanMedia()
        }
    }

    fun scanMedia() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                musicRepository.scanDeviceAudio()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(song: SongEntity) {
        viewModelScope.launch {
            musicRepository.setFavorite(song.id, !song.isFavorite)
        }
    }

    fun setCurrentPlayingSong(songId: Long?) {
        _currentPlayingSongId.value = songId
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
