package com.github.korblu.astrud.ui.viewmodels

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.github.korblu.astrud.data.media.MediaService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private companion object {
        const val TAG = "NowPlayingViewModel"
        const val POSITION_UPDATE_INTERVAL_MS = 50L
    }

    private var mediaControllerFuture: ListenableFuture<MediaController>? = null
    private var _mediaController: MediaController? = null
    val mediaController: MediaController?
        get() = _mediaController

    private val _isControllerConnected = MutableStateFlow(false)
    val isControllerConnected: StateFlow<Boolean> = _isControllerConnected.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentMediaItem = MutableStateFlow<MediaItem?>(null)
    val currentMediaItem: StateFlow<MediaItem?> = _currentMediaItem.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private var positionUpdateJob: Job? = null

    private val _fullDuration = MutableStateFlow(0L)
    val fullDuration: StateFlow<Long> = _fullDuration.asStateFlow()

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
            if (isPlaying) {
                Log.d(TAG, "Playback started.")
                startPositionUpdates()
            } else {
                Log.d(TAG, "Playback stopped/paused.")
                stopPositionUpdates()
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            _currentMediaItem.value = mediaItem
            _currentPosition.value = 0L
            _fullDuration.value = _mediaController?.duration ?: 0L
            if (mediaItem != null) {
                Log.d(TAG, "Media item transitioned: ${mediaItem.mediaMetadata.title}")
                if (_isPlaying.value) {
                    startPositionUpdates()
                }
            } else {
                Log.d(TAG, "Media item transitioned to null.")
                stopPositionUpdates()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_IDLE -> {
                    Log.d(TAG, "Player state: IDLE")
                    stopPositionUpdates()
                }
                Player.STATE_BUFFERING -> Log.d(TAG, "Player state: BUFFERING")
                Player.STATE_READY -> {
                    Log.d(TAG, "Player state: READY")

                    _fullDuration.value = _mediaController?.duration ?: 0L
                }
                Player.STATE_ENDED -> {
                    Log.d(TAG, "Player state: ENDED")
                    _isPlaying.value = false
                    _currentPosition.value = _mediaController?.duration ?: 0L
                    stopPositionUpdates()
                }
            }
        }
    }

    init {
        Log.d(TAG, "ViewModel initialized. Attempting to connect MediaController.")
        initializeController()
    }

    @OptIn(UnstableApi::class)
    private fun initializeController() {
        if (mediaControllerFuture != null || _mediaController != null) {
            Log.d(TAG, "Controller already initializing or initialized.")
            return
        }

        viewModelScope.launch {
            val serviceComponent = ComponentName(context, MediaService::class.java)
            val sessionToken = SessionToken(context, serviceComponent)
            Log.d(TAG, "Creating MediaController for SessionToken: ${sessionToken.packageName}/${sessionToken.serviceName}")

            mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
            mediaControllerFuture?.addListener(
                {
                    try {
                        Log.d(TAG, "MediaController future completed.")
                        val controller = mediaControllerFuture?.get()

                        if (controller != null && controller.isConnected) {
                            _mediaController = controller
                            _mediaController?.addListener(playerListener)
                            _isControllerConnected.value = true
                            _isPlaying.value = controller.isPlaying
                            _currentMediaItem.value = controller.currentMediaItem
                            _currentPosition.value = controller.currentPosition
                            _fullDuration.value = controller.duration
                            Log.i(TAG, "MediaController connected successfully to ${controller.sessionActivity}")
                            if (controller.isPlaying) {
                                startPositionUpdates()
                            }
                        } else {
                            _isControllerConnected.value = false
                            Log.e(TAG, "Failed to connect MediaController or controller is not connected after future completion. Controller null: ${controller == null}")
                        }
                    } catch (e: Exception) {
                        _isControllerConnected.value = false
                        Log.e(TAG, "Error initializing MediaController from future: ${e.message}", e)
                    }
                },
                MoreExecutors.directExecutor()
            )
        }
    }

    private fun startPositionUpdates() {
        stopPositionUpdates()
        positionUpdateJob = viewModelScope.launch {
            while (_isPlaying.value && _mediaController != null && _mediaController!!.isConnected) {
                _currentPosition.value = _mediaController!!.currentPosition
                delay(POSITION_UPDATE_INTERVAL_MS)
            }
        }
        Log.d(TAG, "Started position updates.")
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
        Log.d(TAG, "Stopped position updates.")
    }

    fun playPause() {
        if (!_isControllerConnected.value) {
            Log.w(TAG, "MediaController not connected. Cannot toggle play/pause.")
            return
        }
        if (_mediaController?.isPlaying == true) {
            _mediaController?.pause()
        } else {
            _mediaController?.play()
        }
    }

    fun playSong(
        mediaUri: Uri,
        title: String,
        artist: String,
        album: String?,
        artwork: Uri?,
        mediaId: String = mediaUri.toString()
    ) {
        if (!_isControllerConnected.value) {
            Log.w(TAG, "MediaController not connected. Cannot play song: $title.")
            return
        }
        Log.d(TAG, "playSong called for: $title by $artist")
        val mediaItem = MediaItem.Builder()
            .setUri(mediaUri)
            .setMediaId(mediaId)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumTitle(album)
                    .setArtworkUri(artwork)
                    .build()
            )
            .build()

        _mediaController?.setMediaItem(mediaItem)
        _mediaController?.prepare()
        _mediaController?.play()

        Log.i(TAG, "Playing song: ${mediaItem.mediaMetadata.title}")
    }

    fun seekTo(positionMs: Long) {
        if (!_isControllerConnected.value) {
            Log.w(TAG, "MediaController not connected. Cannot seek.")
            return
        }
        _mediaController?.pause()
        _mediaController?.seekTo(positionMs)
        _currentPosition.value = positionMs
        Log.d(TAG, "Seeking to: $positionMs ms")
    }

    fun skipToNext() {
        if (!_isControllerConnected.value) {
            Log.w(TAG, "MediaController not connected. Cannot skip to next.")
            return
        }
        _mediaController?.seekToNextMediaItem()
        Log.d(TAG, "Skipping to next media item.")
    }

    fun skipToPrevious() {
        if (!_isControllerConnected.value) {
            Log.w(TAG, "MediaController not connected. Cannot skip to previous.")
            return
        }
        if (_mediaController?.hasPreviousMediaItem() == true) {
            seekTo(0)
        } else {
            _mediaController?.seekTo(0)
            _currentPosition.value = 0
        }
        Log.d(TAG, "Skipping to previous media item.")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared. Releasing MediaController.")
        stopPositionUpdates()
        _mediaController?.removeListener(playerListener)
        mediaControllerFuture?.let {
            MediaController.releaseFuture(it)
            Log.d(TAG, "MediaController future released.")
        }
        _mediaController = null
        mediaControllerFuture = null
        _isControllerConnected.value = false
        _isPlaying.value = false
        _currentMediaItem.value = null
        _currentPosition.value = 0L
        _fullDuration.value = 0L
        Log.d(TAG, "NowPlayingViewModel cleared and MediaController resources released.")
    }
}
