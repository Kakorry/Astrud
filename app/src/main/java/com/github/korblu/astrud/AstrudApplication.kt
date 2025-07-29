package com.github.korblu.astrud

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.provider.MediaStore
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath
import com.github.korblu.astrud.ui.viewmodels.PlayerViewModel
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
    class StarShape : Shape {
        override fun createOutline(
            size: androidx.compose.ui.geometry.Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline {
            val shape = RoundedPolygon.star(
                numVerticesPerRadius = 8,
                radius = size.minDimension / 2f,
                innerRadius = size.minDimension / 2.5f,
                centerX = size.width / 2f,
                centerY = size.height / 2f,
                rounding = CornerRounding(
                    radius = size.minDimension * 0.1f,
                    smoothing = 0.2f
                )
            )
            return Outline.Generic(shape.toPath().asComposePath())
        }
    }

    @Composable
    fun StarButton(
        modifier: Modifier = Modifier,
        buttonColor: Color = MaterialTheme.colorScheme.primary,
        iconColor: Color = MaterialTheme.colorScheme.onPrimary,
        buttonSize: Dp = 150.dp,
        star: StarShape = StarShape(),
        playerViewModel: PlayerViewModel
        ) {
        Box(
            modifier = modifier
                .heightIn(max = buttonSize)
                .widthIn(max = buttonSize)
                .fillMaxSize()
                .drawWithCache {
                    val shape = RoundedPolygon.star(
                        numVerticesPerRadius = 8,
                        radius = size.minDimension / 2f,
                        innerRadius = size.minDimension / 2.5f,
                        centerX = size.width / 2f,
                        centerY = size.height / 2f,
                        rounding = CornerRounding(
                            radius = size.minDimension * 0.1f,
                            smoothing = 0.2f
                        )
                    )
                    val path = shape.toPath().asComposePath()
                    onDrawBehind {
                        drawPath(path, color = buttonColor)
                    }
                }
                .aspectRatio(1f)
                .clip(shape = star)
                .clickable(
                    enabled = true,
                    onClick = {
                        playerViewModel.playPause()
                    },
                )
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(buttonSize / 2),
                imageVector = if (playerViewModel.isPlaying.collectAsState().value) {
                    Icons.Rounded.Pause
                } else Icons.Rounded.PlayArrow,
                tint = iconColor,
                contentDescription = "Play"
            )
        }
    }

    // For future use when handling screen size. -K 07/21/2025
    val SCREEN_WIDTH_DP_EXTRA_SMALL = 360.dp
    val SCREEN_WIDTH_DP_SMALL = 411.dp
    val SCREEN_WIDTH_DP_MEDIUM = 600.dp
    val SCREEN_WIDTH_DP_LARGE = 840.dp
    val SCREEN_WIDTH_DP_EXTRA_LARGE = 1200.dp
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