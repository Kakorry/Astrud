package com.github.korblu.astrud.ui.viewmodels

import android.content.Context
import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.korblu.astrud.AppConstants
import com.github.korblu.astrud.data.datastore.LayoutSong
import com.github.korblu.astrud.data.media.UserSongs
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
): ViewModel() {
    private val _layoutSongObjectList = MutableStateFlow<List<LayoutSong>>(listOf())
    val layoutSongObjectList = _layoutSongObjectList.asStateFlow()

    private val _homeListState = MutableStateFlow(LazyListState())
    val homeListState = _homeListState.asStateFlow()

    fun setNewState(lazyState: LazyListState) {
        _homeListState.value = lazyState
    }

    private val _suggestionList = MutableStateFlow<List<Map<String?, String?>?>>(List(10) { mapOf(null to null) })
    val suggestionList = _suggestionList.asStateFlow()

    private val _dialList = MutableStateFlow<List<Map<String?, String?>?>>(List(9) { mapOf(null to null) })
    val dialList = _dialList.asStateFlow()


    fun getRandomDial(userSongs: UserSongs) {
        viewModelScope.launch {
            userSongs.setCursor(
                AppConstants.BASE_PROJECTION,
                arrayOf("YEAR"),
                "RANDOM"
            )
            _dialList.value = List (9) { userSongs.getRandomSong() }
            userSongs.closeCursor()
        }
    }

    fun getRandomSuggestions(userSongs: UserSongs) {
        viewModelScope.launch {
            userSongs.setCursor(
                AppConstants.BASE_PROJECTION,
                arrayOf("YEAR"),
                "RANDOM"
            )
            _suggestionList.value = List (10) { userSongs.getRandomSong() }
            userSongs.closeCursor()
        }
    }
}