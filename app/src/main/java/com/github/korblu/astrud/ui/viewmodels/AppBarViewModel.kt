package com.github.korblu.astrud.ui.viewmodels

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@HiltViewModel
class AppBarViewModel @Inject constructor() : ViewModel() {
    private val _listBarState = MutableStateFlow(LazyListState())
    val listBarState = _listBarState.asStateFlow()

    private val _playedBarAnimation = MutableStateFlow(false)
    val playedBarAnimation = _playedBarAnimation.asStateFlow()

    init {
        _playedBarAnimation.value = false
    }

    private val _homeBarState = MutableStateFlow(LazyListState())
    val homeBarState = _homeBarState.asStateFlow()

    fun onResetPlayedStatus() {
        _playedBarAnimation.value = false
    }

    fun onSetPlayedStatus() {
        _playedBarAnimation.value = true
    }
}