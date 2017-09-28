package se.asapehrsson.podtest.backgroundservice;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

/**
 * Helper APIs for constructing MediaStyle notifications
 */
public class MediaStyleHelper {
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

}