package se.asapehrsson.podtest.backgroundservice;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import se.asapehrsson.podtest.R;

public class AudioService extends MediaBrowserServiceCompat implements MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    public static final String COMMAND_EXAMPLE = "command_example";
    public static final String MEDIA_SESSION_ID = "Tag";
    private static final String TAG = AudioService.class.getSimpleName();
    private MediaPlayer mediaPlayer;
    private MediaSessionCompat mediaSession;
    private int currentQueueIndex = -1;

    private BroadcastReceiver noisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    };
    private ArrayList<MediaSessionCompat.QueueItem> mediaQueue = new ArrayList<>();
    private MediaSessionCompat.Callback mediaSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPrepare() {
            super.onPrepare();
        }

        @Override
        public void onAddQueueItem(MediaDescriptionCompat description) {
            super.onAddQueueItem(description);
            mediaQueue.add(new MediaSessionCompat.QueueItem(description, Integer.parseInt(description.getMediaId())));
        }

        @Override
        public void onStop() {
            super.onStop();
            setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED);

            mediaPlayer.release();
            mediaPlayer = null;
        }

        @Override
        public void onPlay() {
            super.onPlay();
            if (!successfullyRetrievedAudioFocus()) {
                return;
            }

            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);

            showPlayingNotification();
            mediaPlayer.start();
        }

        @Override
        public void onPause() {
            super.onPause();

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
                showPausedNotification();
            }
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
            //NOT USED
        }

        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
            super.onCommand(command, extras, cb);
            if (COMMAND_EXAMPLE.equalsIgnoreCase(command)) {
                //Custom command here
            }
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            mediaPlayer.seekTo((int) pos);
        }

        @Override
        public void onSkipToQueueItem(long id) {
            super.onSkipToQueueItem(id);
            for (int i = 0; i < mediaQueue.size(); i++) {
                MediaSessionCompat.QueueItem queueItem = mediaQueue.get(i);
                if (queueItem.getQueueId() == id) {
                    currentQueueIndex = i;
                    setMediaPlaybackState(PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM);
                    loadAndPlay(queueItem.getDescription());
                }
            }
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();

            int newIndex = currentQueueIndex + 1;
            if (newIndex > 0 && newIndex < mediaQueue.size()) {
                currentQueueIndex = newIndex;
                setMediaPlaybackState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT);
                loadAndPlay(mediaQueue.get(newIndex).getDescription());
            }
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            int newIndex = currentQueueIndex - 1;
            if (newIndex > 0 && newIndex < mediaQueue.size()) {
                currentQueueIndex = newIndex;
                setMediaPlaybackState(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS);
                loadAndPlay(mediaQueue.get(newIndex).getDescription());
            }
        }
    };

    private void loadAndPlay(MediaDescriptionCompat mediaDescription) {
        try {
            if (mediaPlayer == null) {
                initMediaPlayer();
            } else {
                mediaPlayer.reset();
            }

            try {
                mediaPlayer.setDataSource(getApplicationContext(), mediaDescription.getMediaUri());

            } catch (IllegalStateException e) {
                mediaPlayer.release();
                initMediaPlayer();
                mediaPlayer.setDataSource(getApplicationContext(), mediaDescription.getMediaUri());
            }

            setMediaSessionMetadata(mediaDescription);
            setMediaPlaybackState(PlaybackStateCompat.STATE_NONE);

            mediaPlayer.prepare();
            mediaPlayer.start();
            if (!mediaSession.isActive()) {
                mediaSession.setActive(true);
            }
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);

        } catch (IOException e) {
            Log.d(TAG, "Got exception: " + e.getMessage());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initMediaPlayer();
        initMediaSession();
        initNoisyReceiver();
    }


    //Not important for general audio service, required for class
    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if (TextUtils.equals(clientPackageName, getPackageName())) {
            return new BrowserRoot(getString(R.string.app_name), null);
        }

        return null;
    }

    //Not important for general audio service, required for class
    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(null);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS: {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                break;
            }
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: {
                mediaPlayer.pause();
                break;
            }
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK: {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(0.3f, 0.3f);
                }
                break;
            }
            case AudioManager.AUDIOFOCUS_GAIN: {
                if (mediaPlayer != null) {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    }
                    mediaPlayer.setVolume(1.0f, 1.0f);
                }
                break;
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mediaSession, intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(this);
        unregisterReceiver(noisyReceiver);
        mediaSession.release();
        NotificationManagerCompat.from(this).cancel(1);
    }

    private void initNoisyReceiver() {
        //Handles headphones coming unplugged. cannot be done through a manifest receiver
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(noisyReceiver, filter);
    }


    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
        } else {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        mediaPlayer.setVolume(1.0f, 1.0f);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition() < 10) {
                    mediaSessionCallback.onSkipToNext();
                }
            }
        });
    }

    private void initMediaSession() {
        ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);
        mediaSession = new MediaSessionCompat(getApplicationContext(), MEDIA_SESSION_ID, mediaButtonReceiver, null);

        mediaSession.setCallback(mediaSessionCallback);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                | MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
        mediaSession.setMediaButtonReceiver(pendingIntent);
        mediaSession.setQueue(mediaQueue);
        setSessionToken(mediaSession.getSessionToken());
    }

    private void setMediaSessionMetadata(MediaDescriptionCompat mediaDescription) {
        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();
        //Notification icon in card
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        //lock screen icon for pre lollipop
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, mediaDescription.getTitle().toString());
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, mediaDescription.getSubtitle().toString());
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ART_URI, mediaDescription.getIconUri().toString());

        long duration = mediaDescription.getExtras().getLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0);
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration);
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, currentQueueIndex + 1);
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, mediaQueue.size() + 1);

        mediaSession.setMetadata(metadataBuilder.build());
    }

    private void showPlayingNotification() {
        NotificationCompat.Builder builder = MediaStyleHelper.from(AudioService.this, mediaSession);
        if (builder == null) {
            return;
        }

        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause",
                MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)));
        builder.setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mediaSession.getSessionToken()));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        NotificationManagerCompat.from(AudioService.this).notify(1, builder.build());
    }

    private void showPausedNotification() {
        NotificationCompat.Builder builder = MediaStyleHelper.from(this, mediaSession);
        if (builder == null) {
            return;
        }

        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)));
        builder.setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mediaSession.getSessionToken()));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        NotificationManagerCompat.from(this).notify(1, builder.build());
    }


    private void setMediaPlaybackState(int state) {
        PlaybackStateCompat.Builder playbackstateBuilder = new PlaybackStateCompat.Builder();
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PAUSE);
        } else {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PLAY);
        }

        if (currentQueueIndex > 0 && currentQueueIndex < mediaQueue.size()) {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
        }
        if ((currentQueueIndex > -1) && (currentQueueIndex + 1) < mediaQueue.size()) {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_SKIP_TO_NEXT);
        }

        long position = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN;
        int playbackSpeed = 0;

        if (mediaPlayer != null) {
            position = mediaPlayer.getCurrentPosition();
            playbackSpeed = mediaPlayer.isPlaying() ? 1 : 0;
        }
        playbackstateBuilder.setState(state, position, playbackSpeed);

        mediaSession.setPlaybackState(playbackstateBuilder.build());
    }


    private boolean successfullyRetrievedAudioFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        int result = audioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        return result == AudioManager.AUDIOFOCUS_GAIN;
    }

}
