package com.github.korblu.astrud.data.media

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.github.korblu.astrud.R // Make sure this R is correct for your project

@UnstableApi
class MediaService: MediaSessionService() {
    private var mediaSession: MediaSession? = null

    companion object {
        private const val CHANNEL_ID = "astrud_media_playback_channel"
        private const val CHANNEL_NAME = "Astrud Media Playback"
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onCreate() {
        super.onCreate()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)

        val player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player).build()

        val mediaNotificationProvider = DefaultMediaNotificationProvider.Builder(this)
            .setChannelId(CHANNEL_ID)
            .build()
        mediaNotificationProvider.setSmallIcon(R.drawable.ic_astrud)
        setMediaNotificationProvider(mediaNotificationProvider)
    }

    override fun onDestroy() {
        mediaSession?.let { session ->
            session.player.release()
            session.release()
            mediaSession = null
        }

        super.onDestroy()
    }
}
