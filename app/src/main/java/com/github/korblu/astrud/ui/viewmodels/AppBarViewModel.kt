package com.github.korblu.astrud.ui.viewmodels

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@HiltViewModel
class AppBarViewModel @Inject constructor() : ViewModel() {

    private val _barVisibility = MutableStateFlow(true)
    val barVisibility = _barVisibility.asStateFlow()

    private val _listBarState = MutableStateFlow(LazyListState())
    val listBarState = _listBarState.asStateFlow()

    private val _mediaItemVisible = MutableStateFlow(false)
    val mediaItemVisible = _mediaItemVisible.asStateFlow()

    private val _playedBarAnimation = MutableStateFlow(false)
    val playedBarAnimation = _playedBarAnimation.asStateFlow()

    private val _nowPlayingClickState = MutableStateFlow(false)
    val nowPlayingClickState = _nowPlayingClickState.asStateFlow()

    init {
        _playedBarAnimation.value = false
    }

    fun onResetPlayedStatus() {
        _playedBarAnimation.value = false
    }

    fun onHideBars() {
        _barVisibility.value = false
    }

    fun onShowBars() {
        _barVisibility.value = true
    }

    fun onToggleBars() {
        _barVisibility.value = !_barVisibility.value
    }

    fun onSetPlayedStatus() {
        _playedBarAnimation.value = true
    }

    fun onClickedNowPlaying() {
        _nowPlayingClickState.value = true
    }

    fun onResetNowPlaying() {
        _nowPlayingClickState.value = false
    }

    fun onSetMediaItemVisibility() {
        _mediaItemVisible.value = true
    }

    fun onResetMediaItemVisibility() {
        _mediaItemVisible.value = false
    }

    fun onNavigateToNowPlaying(
        navController: NavController,
        uri: String?,
        title: String?,
        artist: String?,
        artwork: String?
    ) {
        navController.navigate("NowPlayingScreen/$uri/$title/$artist/$artwork")
    }
}