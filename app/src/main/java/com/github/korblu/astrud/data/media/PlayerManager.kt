package com.github.korblu.astrud.data.media

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.github.korblu.astrud.AppConstants
import com.github.korblu.astrud.MainActivity
import com.github.korblu.astrud.R

@UnstableApi
class MediaService: MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private var playerNotificationManager: PlayerNotificationManager? = null

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MediaService", "onCreate started")
        val player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player).build()

        PlayerNotificationManager.Builder(
            this,
            AppConstants.MEDIA_NOTIFICATION_ID,
            AppConstants.MEDIA_NOTIFICATION_CHANNEL_ID
        )
            .setSmallIconResourceId(R.drawable.astrud_icon)
            .setMediaDescriptionAdapter(object : PlayerNotificationManager.MediaDescriptionAdapter {
            override fun getCurrentContentTitle(player: Player): CharSequence {
                val mediaItem = player.currentMediaItem
                return mediaItem?.mediaMetadata?.title ?: "Unknown"
            }

            override fun getCurrentContentText(player: Player): CharSequence? {
                val mediaItem = player.currentMediaItem
                return mediaItem?.mediaMetadata?.artist ?: "Unknown"
            }

            override fun createCurrentContentIntent(player: Player): PendingIntent? {
                val openAppIntent = Intent(applicationContext, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                return PendingIntent.getActivity(
                    applicationContext,
                    0,
                    openAppIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

            override fun getCurrentLargeIcon(
                player: Player,
                callback: PlayerNotificationManager.BitmapCallback
            ): Bitmap? {
                return null
            }
        })
            .setNotificationListener(object: PlayerNotificationManager.NotificationListener {
                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    if (ongoing) {
                        startForeground(notificationId, notification)
                    } else {
                        stopForeground(STOP_FOREGROUND_REMOVE)
                    }
                }

                override fun onNotificationCancelled(
                    notificationId: Int,
                    dismissedByUser: Boolean
                ) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }).build().also {
                it.setPlayer(player)
                it.setMediaSessionToken(mediaSession?.platformToken ?: return)
                playerNotificationManager = it
            }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        playerNotificationManager?.setPlayer(null)
        super.onDestroy()
    }
}