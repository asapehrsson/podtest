package se.asapehrsson.podtest.backgroundservice;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import se.asapehrsson.podtest.R;

/**
 * Helper APIs for constructing MediaStyle notifications
 */
public class NotificationHelper {

    public static final int NOTIFICATION_ID = 1;

    /**
     * Build a notification using the information from the given media session. Makes heavy use
     * of {@link MediaMetadataCompat#getDescription()} to extract the appropriate information.
     *
     * @param context      Context used to construct the notification.
     * @param mediaSession Media session to get information.
     * @return A pre-built notification with information from the given media session.
     */
    public static NotificationCompat.Builder from(Context context, MediaSessionCompat mediaSession) {

        NotificationCompat.Builder builder = null;
        try {
            MediaControllerCompat controller = mediaSession.getController();
            MediaMetadataCompat mediaMetadata = controller.getMetadata();
            MediaDescriptionCompat description = mediaMetadata.getDescription();

            builder = new NotificationCompat.Builder(context, "");
            builder
                    .setContentTitle(description.getTitle())
                    .setContentText(description.getSubtitle())
                    .setSubText(description.getDescription())
                    .setLargeIcon(description.getIconBitmap())
                    .setContentIntent(controller.getSessionActivity())
                    .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder;
    }

    public static void showPlayingNotification(Context context, MediaSessionCompat mediaSession, Boolean canSkipBack, Boolean canSkipForward) {
        NotificationCompat.Builder builder = NotificationHelper.from(context, mediaSession);
        if (builder == null) {
            return;
        }

        builder.addAction(new NotificationCompat.Action((canSkipBack? R.drawable.ic_previous : R.drawable.ic_previous_inactive), "Previous",   MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)));
        builder.addAction(new NotificationCompat.Action(R.drawable.ic_pause, "Pause",   MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PLAY_PAUSE)));
        builder.addAction(new NotificationCompat.Action((canSkipForward? R.drawable.ic_next : R.drawable.ic_next_inactive), "Next",   MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)));
        builder.setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mediaSession.getSessionToken()));
        builder.setSmallIcon(R.mipmap.ic_launcher);

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build());
    }

    public static void showPausedNotification(MediaSessionCompat mediaSession, Context context, Boolean canSkipBack, Boolean canSkipForward) {
        NotificationCompat.Builder builder = NotificationHelper.from(context, mediaSession);
        if (builder == null) {
            return;
        }

        builder.addAction(new NotificationCompat.Action((canSkipBack? R.drawable.ic_previous : R.drawable.ic_previous_inactive), "Previous",   MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)));
        builder.addAction(new NotificationCompat.Action(R.drawable.ic_play, "Play", MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PLAY_PAUSE)));
        builder.addAction(new NotificationCompat.Action((canSkipForward? R.drawable.ic_next : R.drawable.ic_next_inactive), "Next",   MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)));

        builder.setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mediaSession.getSessionToken()));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build());
    }
}