package se.asapehrsson.podtest.backgroundservice

import android.content.Context
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat

import se.asapehrsson.podtest.R

/**
 * Helper APIs for constructing MediaStyle notifications
 */
object NotificationHelper {

    val NOTIFICATION_ID = 1

    fun from(context: Context, mediaSession: MediaSessionCompat): NotificationCompat.Builder? {

        var builder: NotificationCompat.Builder? = null
        try {
            val controller = mediaSession.controller
            val mediaMetadata = controller.metadata
            val description = mediaMetadata.description

            builder = NotificationCompat.Builder(context, "SR")
            builder
                    .setContentTitle(description.title)
                    .setContentText(description.subtitle)
                    .setSubText(description.description)
                    .setLargeIcon(description.iconBitmap)
                    .setContentIntent(controller.sessionActivity)
                    .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return builder
    }

    fun showPlayingNotification(context: Context, mediaSession: MediaSessionCompat, canSkipBack: Boolean, canSkipForward: Boolean) {
        val builder = NotificationHelper.from(context, mediaSession) ?: return

        builder.addAction(NotificationCompat.Action(if (canSkipBack) R.drawable.ic_previous else R.drawable.ic_previous_inactive, "Previous", MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)))
        builder.addAction(NotificationCompat.Action(R.drawable.ic_pause, "Pause", MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PLAY_PAUSE)))
        builder.addAction(NotificationCompat.Action(if (canSkipForward) R.drawable.ic_next else R.drawable.ic_next_inactive, "Next", MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)))
        builder.setStyle(android.support.v4.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mediaSession.sessionToken))
        builder.setSmallIcon(R.mipmap.ic_launcher)

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }

    fun showPausedNotification(context: Context, mediaSession: MediaSessionCompat, canSkipBack: Boolean, canSkipForward: Boolean) {
        val builder = NotificationHelper.from(context, mediaSession) ?: return

        builder.addAction(NotificationCompat.Action(if (canSkipBack) R.drawable.ic_previous else R.drawable.ic_previous_inactive, "Previous", MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)))
        builder.addAction(NotificationCompat.Action(R.drawable.ic_play, "Play", MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PLAY_PAUSE)))
        builder.addAction(NotificationCompat.Action(if (canSkipForward) R.drawable.ic_next else R.drawable.ic_next_inactive, "Next", MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)))

        builder.setStyle(android.support.v4.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mediaSession.sessionToken))
        builder.setSmallIcon(R.mipmap.ic_launcher)
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }
}