package com.github.korblu.astrud.data.media

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class ExoPlayerManager(private val context: Context) {
    private var exoPlayer: ExoPlayer? = null

        val player: ExoPlayer?
        get() = exoPlayer

    fun initPlayer() {
        exoPlayer = ExoPlayer.Builder(context).build()
    }

    fun playAudio(uri: String) {
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        exoPlayer?.playWhenReady = true
    }
}