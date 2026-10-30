package com.asta.cherrymusicplayer.ui.viewmodel

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.asta.cherrymusicplayer.data.entity.SongEntity
import com.asta.cherrymusicplayer.data.preferences.PreferencesManager
import com.asta.cherrymusicplayer.data.repository.MusicRepository
import com.asta.cherrymusicplayer.service.MusicService
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

data class PlaybackProgress(val position: Long = 0L, val duration: Long = 0L) {
    val fraction: Float get() = if (duration > 0) (position.toFloat() / duration).coerceIn(0f, 1f) else 0f
}

data class PlayerUiState(
    val currentSong: SongEntity? = null,
    val isPlaying: Boolean = false,
    val queue: List<SongEntity> = emptyList(),
    val currentIndex: Int = 0,
    val shuffleModeEnabled: Boolean = false,
    val repeatMode: Int = Player.REPEAT_MODE_OFF,
    val volume: Float = 1f,
    val sleepTimerMinutes: Int? = null,
    val sleepTimerRemainingMillis: Long? = null
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val musicRepository: MusicRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private val _progress = MutableStateFlow(PlaybackProgress())
    val progress: StateFlow<PlaybackProgress> = _progress.asStateFlow()

    val allSongs = musicRepository.allSongs
    val favoriteSongs = musicRepository.favoriteSongs
    val allPlaylists = musicRepository.allPlaylists

    private var mediaController: MediaController? = null
    private var positionJob: Job? = null
    private var sleepTimerJob: Job? = null
    private var cachedSongsList: List<SongEntity> = emptyList()

    init {
        initializeController()
        viewModelScope.launch {
            _progress.subscriptionCount.collect { count ->
                if (count > 0) startPositionUpdates() else stopPositionUpdates()
            }
        }
        viewModelScope.launch {
            musicRepository.allSongs.collect { songs ->
                cachedSongsList = songs
                updateSongFromMediaItem(mediaController?.currentMediaItem)
            }
        }
    }

    private fun initializeController() {
        viewModelScope.launch {
            try {
                val sessionToken = SessionToken(
                    context,
                    ComponentName(context, MusicService::class.java)
                )
                val controller = MediaController.Builder(context, sessionToken).buildAsync().await()
                mediaController = controller
                setupPlayerListener(controller)
                
                val savedSpeed = preferencesManager.playbackSpeedFlow.first()
                controller.setPlaybackSpeed(savedSpeed)

                updateStateFromController(controller)
                if (_progress.subscriptionCount.value > 0) startPositionUpdates()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun <T> ListenableFuture<T>.await(): T =
        suspendCancellableCoroutine { continuation ->
            val listener = Runnable {
                try {
                    continuation.resume(get())
                } catch (t: Throwable) {
                    continuation.resumeWith(Result.failure(t.cause ?: t))
                }
            }
            addListener(listener, Runnable::run)
            continuation.invokeOnCancellation {
                cancel(false)
            }
        }

    private fun setupPlayerListener(controller: MediaController) {
        controller.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _uiState.update { it.copy(isPlaying = isPlaying) }
                syncProgress()
                if (isPlaying && _progress.subscriptionCount.value > 0) startPositionUpdates() else stopPositionUpdates()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                syncProgress()
                updateSongFromMediaItem(mediaItem)
                _uiState.update { it.copy(currentIndex = controller.currentMediaItemIndex) }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                updateStateFromController(controller)
            }

            override fun onPositionDiscontinuity(oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) {
                syncProgress()
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _uiState.update { it.copy(shuffleModeEnabled = shuffleModeEnabled) }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                _uiState.update { it.copy(repeatMode = repeatMode) }
            }
        })
    }

    private fun updateStateFromController(controller: MediaController) {
        _uiState.update {
            it.copy(
                isPlaying = controller.isPlaying,
                currentIndex = controller.currentMediaItemIndex,
                shuffleModeEnabled = controller.shuffleModeEnabled,
                repeatMode = controller.repeatMode
            )
        }
        val songsById = cachedSongsList.associateBy { it.id }
        val queue = (0 until controller.mediaItemCount).map { index ->
            val item = controller.getMediaItemAt(index)
            songsById[item.mediaId.toLongOrNull()] ?: songFromMediaItem(item)
        }
        _uiState.update { it.copy(queue = queue) }
        syncProgress()
        updateSongFromMediaItem(controller.currentMediaItem)
    }

    private fun updateSongFromMediaItem(mediaItem: MediaItem?) {
        val id = mediaItem?.mediaId?.toLongOrNull()
        val song = cachedSongsList.find { it.id == id } ?: mediaItem?.let(::songFromMediaItem)
        _uiState.update { it.copy(currentSong = song) }
    }

    private fun songFromMediaItem(item: MediaItem): SongEntity = SongEntity(
        id = item.mediaId.toLongOrNull() ?: 0L,
        title = item.mediaMetadata.title?.toString() ?: "Unknown title",
        artist = item.mediaMetadata.artist?.toString() ?: "Unknown artist",
        album = item.mediaMetadata.albumTitle?.toString() ?: "Unknown album",
        duration = 0L,
        data = item.localConfiguration?.uri?.toString() ?: "",
        albumArtUri = item.mediaMetadata.artworkUri?.toString()
    )

    private fun syncProgress() {
        val controller = mediaController ?: return
        _progress.value = PlaybackProgress(controller.currentPosition.coerceAtLeast(0L), controller.duration.coerceAtLeast(0L))
    }

    private fun startPositionUpdates() {
        positionJob?.cancel()
        syncProgress()
        if (mediaController?.isPlaying != true) return
        positionJob = viewModelScope.launch {
            while (true) {
                syncProgress()
                delay(250L)
            }
        }
    }

    private fun stopPositionUpdates() {
        positionJob?.cancel()
    }

    fun setAllSongsList(songs: List<SongEntity>) {
        cachedSongsList = songs
    }

    fun playSong(song: SongEntity, queue: List<SongEntity> = listOf(song)) {
        val controller = mediaController ?: return
        cachedSongsList = if (queue.isNotEmpty()) queue else cachedSongsList
        val mediaItems = cachedSongsList.map { s ->
            MediaItem.Builder()
                .setMediaId(s.id.toString())
                .setUri(Uri.parse(s.data))
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(s.title)
                        .setArtist(s.artist)
                        .setAlbumTitle(s.album)
                        .setArtworkUri(s.albumArtUri?.let { Uri.parse(it) })
                        .build()
                )
                .build()
        }
        val startIndex = cachedSongsList.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
        controller.setMediaItems(mediaItems, startIndex, 0L)
        controller.prepare()
        controller.play()
        _uiState.update {
            it.copy(
                queue = cachedSongsList,
                currentIndex = startIndex,
                currentSong = song,
                isPlaying = true
            )
        }
    }

    fun playQueue(queue: List<SongEntity>, startIndex: Int = 0) {
        if (queue.isEmpty()) return
        playSong(queue[startIndex.coerceIn(0, queue.size - 1)], queue)
    }

    fun togglePlayPause() {
        val controller = mediaController ?: return
        if (controller.isPlaying) {
            controller.pause()
        } else {
            if (controller.playbackState == Player.STATE_IDLE) {
                controller.prepare()
            }
            controller.play()
        }
    }

    fun skipToNext() {
        mediaController?.seekToNextMediaItem()
    }

    fun skipToPrevious() {
        mediaController?.seekToPreviousMediaItem()
    }

    fun seekTo(position: Long) {
        mediaController?.seekTo(position)
        syncProgress()
    }

    fun toggleShuffle() {
        val controller = mediaController ?: return
        controller.shuffleModeEnabled = !controller.shuffleModeEnabled
        _uiState.update { it.copy(shuffleModeEnabled = controller.shuffleModeEnabled) }
    }

    fun toggleRepeatMode() {
        val controller = mediaController ?: return
        val newMode = when (controller.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
        controller.repeatMode = newMode
        _uiState.update { it.copy(repeatMode = newMode) }
    }

    fun toggleFavorite(song: SongEntity) {
        viewModelScope.launch {
            musicRepository.setFavorite(song.id, !song.isFavorite)
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            musicRepository.insertPlaylist(name)
        }
    }

    fun renamePlaylist(playlistId: Long, newName: String) {
        viewModelScope.launch {
            musicRepository.updatePlaylist(playlistId, newName)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            musicRepository.deletePlaylist(playlistId)
        }
    }

    fun addSongToPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            musicRepository.addSongToPlaylist(playlistId, songId)
        }
    }

    fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            musicRepository.removeSongFromPlaylist(playlistId, songId)
        }
    }

    fun getSongsForPlaylist(playlistId: Long) = musicRepository.getSongsForPlaylist(playlistId)

    fun setSleepTimer(minutes: Int) {
        sleepTimerJob?.cancel()
        if (minutes <= 0) {
            _uiState.update { it.copy(sleepTimerMinutes = null, sleepTimerRemainingMillis = null) }
            return
        }
        val totalMillis = minutes * 60 * 1000L
        _uiState.update { it.copy(sleepTimerMinutes = minutes, sleepTimerRemainingMillis = totalMillis) }
        sleepTimerJob = viewModelScope.launch {
            var remaining = totalMillis
            while (remaining > 0) {
                delay(1000L)
                remaining -= 1000L
                _uiState.update { it.copy(sleepTimerRemainingMillis = remaining) }
            }
            mediaController?.pause()
            _uiState.update { it.copy(sleepTimerMinutes = null, sleepTimerRemainingMillis = null) }
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        mediaController?.setPlaybackSpeed(speed)
        viewModelScope.launch {
            preferencesManager.setPlaybackSpeed(speed)
        }
    }

    fun setVolume(volume: Float) {
        mediaController?.volume = volume.coerceIn(0f, 1f)
        _uiState.update { it.copy(volume = volume) }
    }

    override fun onCleared() {
        super.onCleared()
        positionJob?.cancel()
        sleepTimerJob?.cancel()
        mediaController?.release()
        mediaController = null
    }
}
