package com.asta.cherrymusicplayer.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.asta.cherrymusicplayer.data.dao.MusicDao
import com.asta.cherrymusicplayer.data.entity.PlaylistEntity
import com.asta.cherrymusicplayer.data.entity.PlaylistSongCrossRef
import com.asta.cherrymusicplayer.data.entity.SongEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

data class Album(
    val id: Long,
    val title: String,
    val artist: String,
    val artUri: String?,
    val songCount: Int,
    val songs: List<SongEntity>
)

data class Artist(
    val name: String,
    val songCount: Int,
    val albumCount: Int,
    val songs: List<SongEntity>
)

@Singleton
class MusicRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val musicDao: MusicDao
) {

    val allSongs: Flow<List<SongEntity>> = musicDao.getAllSongs()
    val favoriteSongs: Flow<List<SongEntity>> = musicDao.getFavoriteSongs()
    val allPlaylists: Flow<List<PlaylistEntity>> = musicDao.getAllPlaylists()

    suspend fun scanDeviceAudio(): List<SongEntity> = withContext(Dispatchers.IO) {
        val songs = mutableListOf<SongEntity>()
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATE_ADDED
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        try {
            context.contentResolver.query(
                collection,
                projection,
                selection,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val rawTitle = cursor.getString(titleColumn) ?: "Unknown Title"
                    val rawArtist = cursor.getString(artistColumn) ?: "Unknown Artist"
                    val rawAlbum = cursor.getString(albumColumn) ?: "Unknown Album"
                    val duration = cursor.getLong(durationColumn)
                    val data = cursor.getString(dataColumn) ?: ""
                    val albumId = cursor.getLong(albumIdColumn)
                    val dateAdded = cursor.getLong(dateAddedColumn) * 1000L

                    val file = File(data)
                    val extension = file.extension.lowercase()
                    if (data.isNotEmpty() && (extension in listOf("mp3", "m4a", "flac", "wav", "ogg") || file.exists())) {
                        val albumArtUri = ContentUris.withAppendedId(
                            Uri.parse("content://media/external/audio/albumart"),
                            albumId
                        ).toString()

                        val song = SongEntity(
                            id = id,
                            title = rawTitle,
                            artist = if (rawArtist == "<unknown>") "Unknown Artist" else rawArtist,
                            album = if (rawAlbum == "<unknown>") "Unknown Album" else rawAlbum,
                            duration = duration,
                            data = data,
                            albumArtUri = albumArtUri,
                            isFavorite = false,
                            dateAdded = if (dateAdded > 0) dateAdded else System.currentTimeMillis()
                        )
                        songs.add(song)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (songs.isNotEmpty()) {
            musicDao.insertSongs(songs)
        }
        songs
    }

    fun getAlbumsFlow(): Flow<List<Album>> = flow {
        allSongs.collect { songs ->
            val albumMap = songs.groupBy { it.album }
            val albums = albumMap.map { (albumName, albumSongs) ->
                val first = albumSongs.first()
                Album(
                    id = first.id,
                    title = albumName,
                    artist = first.artist,
                    artUri = first.albumArtUri,
                    songCount = albumSongs.size,
                    songs = albumSongs
                )
            }.sortedBy { it.title }
            emit(albums)
        }
    }.flowOn(Dispatchers.Default)

    fun getArtistsFlow(): Flow<List<Artist>> = flow {
        allSongs.collect { songs ->
            val artistMap = songs.groupBy { it.artist }
            val artists = artistMap.map { (artistName, artistSongs) ->
                val albums = artistSongs.map { it.album }.distinct()
                Artist(
                    name = artistName,
                    songCount = artistSongs.size,
                    albumCount = albums.size,
                    songs = artistSongs
                )
            }.sortedBy { it.name }
            emit(artists)
        }
    }.flowOn(Dispatchers.Default)

    suspend fun setFavorite(songId: Long, isFavorite: Boolean) {
        musicDao.setFavorite(songId, isFavorite)
    }

    suspend fun insertPlaylist(name: String): Long {
        return musicDao.insertPlaylist(PlaylistEntity(name = name))
    }

    suspend fun updatePlaylist(playlistId: Long, name: String) {
        musicDao.updatePlaylist(PlaylistEntity(id = playlistId, name = name))
    }

    suspend fun deletePlaylist(playlistId: Long) {
        musicDao.deletePlaylistById(playlistId)
    }

    suspend fun addSongToPlaylist(playlistId: Long, songId: Long) {
        musicDao.addSongToPlaylist(PlaylistSongCrossRef(playlistId, songId))
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        musicDao.removeSongFromPlaylist(playlistId, songId)
    }

    fun getSongsForPlaylist(playlistId: Long): Flow<List<SongEntity>> {
        return musicDao.getSongsForPlaylist(playlistId)
    }
}
