package com.github.korblu.astrud

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale
import kotlin.math.roundToInt

object AppConstants {
    data class SongMetadata(
        val uri: Uri?,
        val title: String?,
        val artist: String?,
        val album: String?,
        val albumArtUri: Uri?
    )

    class TimeConverter {
        fun convertProgress(currentProgress: Float, songDuration: Float): String {
            val currentSeconds = (currentProgress * songDuration / 1000).roundToInt()
            val minutes = currentSeconds / 60
            val seconds = currentSeconds % 60

            return String.format(Locale.US, "%01d:%02d", minutes, seconds)
        }

        fun convertFullDuration(songDuration: Int): String {
            val currentSeconds = songDuration / 1000
            val minutes = currentSeconds / 60
            val seconds = currentSeconds % 60

            return String.format(Locale.US, "%01d:%02d", minutes, seconds)
        }
    }
    val BASE_PROJECTION = arrayOf(
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.YEAR
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
class AstrudApplication: Application()