package com.github.korblu.astrud.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.korblu.astrud.data.datastore.UserPreferences
import com.github.korblu.astrud.data.repos.SongsRepo
import com.github.korblu.astrud.data.repos.UserPreferencesRepo
import com.github.korblu.astrud.data.room.SongsEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val songsRepo : SongsRepo,
    private val userPrefRepo : UserPreferencesRepo
) : ViewModel() {
    fun onAddSong(song : SongsEntity) {
        viewModelScope.launch {
            songsRepo.addSong(song)
        }
    }

    fun onUpdateSong(song : SongsEntity) {
        viewModelScope.launch {
            songsRepo.updateSong(song)
        }
    }

    fun onDeleteSong(song : SongsEntity) {
        viewModelScope.launch {
            songsRepo.deleteSong(song)
        }
    }

    fun onGetSongById(id : Long) {
        viewModelScope.launch {
            songsRepo.getSongById(id)
        }
    }

    // For tests. Will probably be removed.
    val userPreferences : StateFlow<UserPreferences> = userPrefRepo.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences.getDefaultInstance()
        )

    fun onUpdateAstrudMusicUri(uri : String) {
        viewModelScope.launch {
            userPrefRepo.updateAstrudMusicUri(uri)
        }
    }

    fun onClearAstrudMusicUri() {
        viewModelScope.launch {
            userPrefRepo.clearAstrudMusicUri()
        }
    }
}
