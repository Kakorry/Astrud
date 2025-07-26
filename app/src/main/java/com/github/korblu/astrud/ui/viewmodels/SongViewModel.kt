package com.github.korblu.astrud.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.korblu.astrud.data.datastore.LayoutSong
import com.github.korblu.astrud.data.datastore.LayoutSongs
import com.github.korblu.astrud.data.datastore.UserPreferences
import com.github.korblu.astrud.data.repos.LayoutSongsRepo
import com.github.korblu.astrud.data.repos.SongsRepo
import com.github.korblu.astrud.data.repos.UserPreferencesRepo
import com.github.korblu.astrud.data.room.RoomSong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val songsRepo: SongsRepo,
    private val userPrefRepo: UserPreferencesRepo,
    private val layoutSongsRepo: LayoutSongsRepo
) : ViewModel() {

    private val _randomRoomSong = MutableStateFlow<RoomSong?>(null)
    val randomRoomSong = _randomRoomSong.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _wasPermissionGiven = MutableStateFlow<Boolean?>(null)
    val wasPermissionGiven = _wasPermissionGiven.asStateFlow()

    private val _layoutSongObjectList = MutableStateFlow<List<LayoutSong>>(listOf())
    val layoutSongObjectList = _layoutSongObjectList.asStateFlow()

    fun onUpdateRoomSong(roomSong: RoomSong) {
        viewModelScope.launch {
            songsRepo.updateSong(roomSong)
        }
    }

    fun onDeleteRoomSong(roomSong: RoomSong) {
        viewModelScope.launch {
            songsRepo.deleteSong(roomSong)
        }
    }

    fun onGetRoomSongById(id: Long) {
        viewModelScope.launch {
            songsRepo.getSongById(id)
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
}
