package com.github.korblu.astrud

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.provider.MediaStore
import androidx.compose.ui.unit.dp
import dagger.hilt.android.HiltAndroidApp

object AppConstants {
    val BASE_PROJECTION = arrayOf(
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.ALBUM,
    )
    const val MEDIA_NOTIFICATION_CHANNEL_ID = "media_playback_channel"
    const val MEDIA_NOTIFICATION_ID = 1965

    // For future use when handling screen size. -K 07/21/2025
    val SCREEN_WIDTH_DP_EXTRA_SMALL = 240.dp
    val SCREEN_WIDTH_DP_SMALL = 360.dp
    val SCREEN_WIDTH_DP_MEDIUM = 432.dp
    val SCREEN_WIDTH_DP_LARGE = 640.dp
    val SCREEN_WIDTH_DP_EXTRA_LARGE = 1280.dp
}

@HiltAndroidApp
class AstrudApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        createMediaNotificationChannel()
    }

    private fun createMediaNotificationChannel() {
        val channel = NotificationChannel(
            AppConstants.MEDIA_NOTIFICATION_CHANNEL_ID,
            "Media Playback",
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}