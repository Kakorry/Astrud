package com.github.korblu.astrud.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.korblu.astrud.data.repos.UserStatsRepo
import com.github.korblu.astrud.data.room.entity.RoomAlbumsPlayed
import com.github.korblu.astrud.data.room.entity.RoomArtistsPlayed
import com.github.korblu.astrud.data.room.entity.RoomOtherStats
import com.github.korblu.astrud.data.room.entity.RoomSongsPlayed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userStatsRepo: UserStatsRepo
) : ViewModel() {
    private val _playedSongsList = MutableStateFlow(listOf<RoomSongsPlayed>())
    val playedSongsList = _playedSongsList.asStateFlow()

    private val _playedAlbumsList = MutableStateFlow(listOf<RoomAlbumsPlayed>())
    val playedAlbumsList = _playedAlbumsList.asStateFlow()

    private val _playedArtistsList = MutableStateFlow(listOf<RoomArtistsPlayed>())
    val playedArtistsList = _playedArtistsList.asStateFlow()

    private val _otherStats = MutableStateFlow<RoomOtherStats?>(null)
    val otherStats = _otherStats.asStateFlow()

    init {
        onRefreshPlayedList()
    }

    fun onIncrementSongsPlayed(song: RoomSongsPlayed) {
        viewModelScope.launch {
            userStatsRepo.incrementSongsPlayed(song)
        }
    }

    fun onIncrementAlbumsPlayed(album: RoomAlbumsPlayed) {
        viewModelScope.launch {
            userStatsRepo.incrementAlbumsPlayed(album)
        }
    }

    fun onIncrementArtistsPlayed(artist: RoomArtistsPlayed) {
        viewModelScope.launch {
            userStatsRepo.incrementArtistsPlayed(artist)
        }
    }

    fun onRefreshPlayedList() {
        viewModelScope.launch {
            _playedSongsList.value = userStatsRepo.getPlayedSongs()
            _playedAlbumsList.value = userStatsRepo.getPlayedAlbums()
            _playedArtistsList.value = userStatsRepo.getPlayedArtists()
            _otherStats.value = userStatsRepo.getOtherStats()
        }
    }

    fun onUpdateOtherStats(stats: RoomOtherStats) {
        viewModelScope.launch {
            userStatsRepo.updateOtherStats(stats)
        }
    }

    fun onUpdatePlayedTotalStat(playedTotal: Int) {
        viewModelScope.launch {
            userStatsRepo.updatePlayedTotalStat(playedTotal)
        }
    }

    fun onUpdateHoursSpent(hoursSpent: Long) {
        viewModelScope.launch {
            userStatsRepo.updateHoursSpent(hoursSpent)
        }
    }
}