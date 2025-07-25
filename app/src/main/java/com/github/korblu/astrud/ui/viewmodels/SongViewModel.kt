package com.github.korblu.astrud.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.korblu.astrud.data.datastore.UserPreferences
import com.github.korblu.astrud.data.repos.SongsRepo
import com.github.korblu.astrud.data.repos.UserPreferencesRepo
import com.github.korblu.astrud.data.room.Song
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
    private val userPrefRepo: UserPreferencesRepo
) : ViewModel() {

    private val _randomSong = MutableStateFlow<Song?>(null)
    val randomSong = _randomSong.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _wasPermissionGiven = MutableStateFlow<Boolean?>(null)
    val wasPermissionGiven = _wasPermissionGiven.asStateFlow()

    fun onUpdateSong(song: Song) {
        viewModelScope.launch {
            songsRepo.updateSong(song)
        }
    }

    fun onDeleteSong(song: Song) {
        viewModelScope.launch {
            songsRepo.deleteSong(song)
        }
    }

    fun onGetSongById(id: Long) {
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
}
