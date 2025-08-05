package com.github.korblu.astrud.ui.viewmodels

import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.korblu.astrud.data.datastore.LayoutSong
import com.github.korblu.astrud.data.datastore.UserPreferences
import com.github.korblu.astrud.data.media.UserSongs
import com.github.korblu.astrud.data.repos.LayoutSongsRepo
import com.github.korblu.astrud.data.repos.RoomRecentsRepo
import com.github.korblu.astrud.data.repos.UserPreferencesRepo
import com.github.korblu.astrud.data.room.entity.RoomRecents
import com.github.korblu.astrud.data.room.room_models.LastPlayedAlbumsInfo
import com.github.korblu.astrud.data.room.room_models.LastPlayedArtistsInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    userPrefRepo: UserPreferencesRepo,
    private val layoutSongsRepo: LayoutSongsRepo,
    private val recentsRepo: RoomRecentsRepo
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _librarySize = MutableStateFlow<Int?>(null)
    val librarySize = _librarySize.asStateFlow()

    private val _wasPermissionGiven = MutableStateFlow<Boolean?>(null)
    val wasPermissionGiven = _wasPermissionGiven.asStateFlow()

    private val _layoutSongObjectList = MutableStateFlow<List<LayoutSong>>(listOf())
    val layoutSongObjectList = _layoutSongObjectList.asStateFlow()

    private val _recentSongsFlow = MutableStateFlow<List<RoomRecents>>(listOf())
    val recentSongsFlow = _recentSongsFlow.asStateFlow()

    private val _recentAlbumsFlow = MutableStateFlow<List<LastPlayedAlbumsInfo>>(listOf())
    val recentAlbumsFlow = _recentAlbumsFlow.asStateFlow()

    private val _recentArtistsFlow = MutableStateFlow<List<LastPlayedArtistsInfo>>(listOf())
    val recentArtistsFlow = _recentArtistsFlow.asStateFlow()

    init {
        viewModelScope.launch {
            _recentSongsFlow.value = recentsRepo.getMostRecents(30)

            _recentAlbumsFlow.value = recentsRepo.getLastPlayedAlbums(30)

            _recentArtistsFlow.value = recentsRepo.getLastPlayedArtists(30)
        }
    }

    // For tests. Will probably be removed.
    val userPreferences: StateFlow<UserPreferences> = userPrefRepo.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences.getDefaultInstance()
        )

    fun onPermissionChange(isGranted: Boolean) {
        viewModelScope.launch {
            _wasPermissionGiven.value = isGranted
        }
    }

    fun onToLayoutSongObject(songs : List<Map<String, String?>>) {
        viewModelScope.launch {
            _layoutSongObjectList.value = layoutSongsRepo.toLayoutSongObject(songs)
        }
    }

    fun onSetLayoutSongs(songsList : List<LayoutSong>) {
        viewModelScope.launch {
            layoutSongsRepo.setSongs(songsList)
        }
    }

    fun onClearLayoutSongs() {
        viewModelScope.launch {
            layoutSongsRepo.clearSongs()
        }
    }

    fun onGetLibrarySize() {
        viewModelScope.launch {
            val userSongs = UserSongs(context)
            userSongs.setCursor(
                projection = arrayOf(
                    MediaStore.Audio.Media.TITLE
                ),
                sort = "ASC"
            )
            val currentLibrarySize = userSongs.getCollectionSize()
            _librarySize.value = currentLibrarySize
            userSongs.closeCursor()
        }
    }

    fun onInsertRecents(song: RoomRecents) {
        viewModelScope.launch {
            recentsRepo.insertOrUpdate(song)
            onUpdateRecents()
        }
    }

    fun onUpdateRecents() {
        viewModelScope.launch {
            _recentSongsFlow.value = recentsRepo.getMostRecents(30)

            _recentAlbumsFlow.value = recentsRepo.getLastPlayedAlbums(30)

            _recentArtistsFlow.value = recentsRepo.getLastPlayedArtists(30)
        }
    }
}
