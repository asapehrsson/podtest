/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.asapehrsson.podtest.backgroundservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Build
import android.os.RemoteException
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.app.NotificationCompat.MediaStyle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import se.asapehrsson.podtest.MainActivity
import se.asapehrsson.podtest.R
import android.support.v4.content.ContextCompat



/**
 * Keeps track of a notification and updates it automatically for a given
 * MediaSession.
 *
 *
 * Originates from universal music player
 */
class MediaNotificationManager(val audioService: AudioService, val queueHandler: MediaQueueHandler) : BroadcastReceiver() {
    private var sessionToken: MediaSessionCompat.Token? = null
    private var mediaController: MediaControllerCompat? = null
    private var transportControls: MediaControllerCompat.TransportControls? = null

    private var playbackState: PlaybackStateCompat? = null
    private var mediaMetadata: MediaMetadataCompat? = null

    private val notificationManager: NotificationManager

    private val playIntent: PendingIntent
    private val pauseIntent: PendingIntent
    private val previousIntent: PendingIntent
    private val nextIntent: PendingIntent
    private val stopIntent: PendingIntent

    private val mNotificationColor: Int = 0

    private var started = false

    init {
        updateSessionToken()

        //mNotificationColor = ResourceHelper.getThemeColor(audioService, R.attr.colorPrimary, Color.DKGRAY);

        notificationManager = audioService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val pkg = audioService.packageName
        pauseIntent = PendingIntent.getBroadcast(audioService, REQUEST_CODE, Intent(ACTION_PAUSE).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT)
        playIntent = PendingIntent.getBroadcast(audioService, REQUEST_CODE, Intent(ACTION_PLAY).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT)
        previousIntent = PendingIntent.getBroadcast(audioService, REQUEST_CODE, Intent(ACTION_PREV).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT)
        nextIntent = PendingIntent.getBroadcast(audioService, REQUEST_CODE, Intent(ACTION_NEXT).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT)
        stopIntent = PendingIntent.getBroadcast(audioService, REQUEST_CODE, Intent(ACTION_STOP).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT)

        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
        notificationManager.cancelAll()
    }

    /**
     * Posts the notification and starts tracking the session to keep it
     * updated. The notification will automatically be removed if the session is
     * destroyed before [.stopNotification] is called.
     */
    fun startNotification(context: Context) {
        if (!started) {
            mediaMetadata = mediaController?.metadata
            playbackState = mediaController?.playbackState

            // The notification must be updated after setting started to true
            val notification = createNotification()
            if (notification != null) {
                mediaController?.registerCallback(mediaControllerCallback)
                val filter = IntentFilter()
                filter.addAction(ACTION_NEXT)
                filter.addAction(ACTION_PAUSE)
                filter.addAction(ACTION_PLAY)
                filter.addAction(ACTION_PREV)

                audioService.registerReceiver(this, filter)

                val intent = Intent(context, AudioService::class.java)
                ContextCompat.startForegroundService(context, intent)
                audioService.startForeground(NOTIFICATION_ID, notification)
                started = true





            }
        }
        if (playbackState?.state == PlaybackStateCompat.STATE_PLAYING || playbackState?.state == PlaybackStateCompat.STATE_PAUSED) {
            val notification = createNotification()
            if (notification != null) {
                notificationManager.notify(NOTIFICATION_ID, notification)
            }
        }
    }

    /**
     * Removes the notification and stops tracking the session. If the session
     * was destroyed this has no effect.
     */
    fun stopNotification() {
        if (started) {
            started = false
            mediaController!!.unregisterCallback(mediaControllerCallback)
            try {
                notificationManager.cancel(NOTIFICATION_ID)
                audioService.unregisterReceiver(this)
            } catch (ex: IllegalArgumentException) {
                // ignore if the receiver is not registered.
            }

            audioService.stopForeground(true)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d(TAG, "Received action " + action)
        when (action) {
            ACTION_PAUSE -> transportControls?.pause()
            ACTION_PLAY -> transportControls?.play()
            ACTION_NEXT -> transportControls?.skipToNext()
            ACTION_PREV -> transportControls?.skipToPrevious()

            else -> Log.w(TAG, "Ignoring unknown action: " + action)
        }
    }

    /**
     * Update the state based on a change on the session token. Called either when
     * we are running for the first time or when the media session owner has destroyed the session
     * (see [android.media.session.MediaController.Callback.onSessionDestroyed])
     */
    @Throws(RemoteException::class)
    private fun updateSessionToken() {
        val freshToken = audioService.sessionToken
        if (sessionToken == null && freshToken != null || sessionToken != null && sessionToken != freshToken) {
            if (mediaController != null) {
                mediaController!!.unregisterCallback(mediaControllerCallback)
            }
            sessionToken = freshToken
            if (sessionToken != null) {
                mediaController = MediaControllerCompat(audioService, sessionToken!!)
                transportControls = mediaController!!.transportControls
                if (started) {
                    mediaController!!.registerCallback(mediaControllerCallback)
                }
            }
        }
    }

    private fun createContentIntent(description: MediaDescriptionCompat?): PendingIntent {
        val openUI = Intent(audioService, MainActivity::class.java)
        openUI.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        if (description != null) {
            //openUI.putExtra(MainActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, description);
        }
        return PendingIntent.getActivity(audioService, REQUEST_CODE, openUI, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    private val mediaControllerCallback = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
            playbackState = state
            Log.d(TAG, "Received new playback state" + state)

            when (state.state) {
                PlaybackStateCompat.STATE_STOPPED -> stopNotification()
                PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.STATE_PAUSED
                -> {
                    val notification = createNotification()
                    if (notification != null) {
                        notificationManager.notify(NOTIFICATION_ID, notification)
                    }
                }
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            mediaMetadata = metadata
            Log.d(TAG, "Received new metadata " + metadata!!)
            val notification = createNotification()
            if (notification != null) {
                notificationManager.notify(NOTIFICATION_ID, notification)
            }
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            Log.d(TAG, "Session was destroyed, resetting to the new session token")
            try {
                updateSessionToken()
            } catch (e: RemoteException) {
                Log.e(TAG, e.message)
            }

        }
    }

    private fun createNotification(): Notification? {
        Log.d(TAG, "updateNotificationMetadata. mediaMetadata=" + mediaMetadata!!)
        if (mediaMetadata == null || playbackState == null) {
            return null
        }

        val description = mediaMetadata!!.description

        // Notification channels are only supported on Android O+.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val notificationBuilder = NotificationCompat.Builder(audioService, CHANNEL_ID)

        val playPauseButtonPosition = addActions(notificationBuilder)
        notificationBuilder
                .setStyle(MediaStyle()
                        // show only play/pause in compact view
                        .setShowActionsInCompactView(playPauseButtonPosition)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(stopIntent)
                        .setMediaSession(sessionToken))
                .setDeleteIntent(stopIntent)
                .setColor(mNotificationColor)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setContentIntent(createContentIntent(description))
                .setContentTitle(description.title)
                .setContentText(description.subtitle)
                .setLargeIcon(BitmapFactory.decodeResource(audioService.resources, R.mipmap.ic_launcher))

        setNotificationPlaybackState(notificationBuilder)
        return notificationBuilder.build()
    }

    private fun addActions(builder: NotificationCompat.Builder): Int {
        Log.d(TAG, "updatePlayPauseAction")
        //For now, since we always show prev, play index is hardcoded
        var playIndex = 1

        builder.addAction(NotificationCompat.Action(if (queueHandler.hasPreviousItem()) R.drawable.ic_previous else R.drawable.ic_previous_inactive, "Previous", previousIntent))

        if (playbackState!!.state == PlaybackStateCompat.STATE_PLAYING) {
            builder.addAction(NotificationCompat.Action(R.drawable.ic_pause, "Pause", pauseIntent))
        } else {
            builder.addAction(NotificationCompat.Action(R.drawable.ic_play, "Play", playIntent))
        }

        builder.addAction(NotificationCompat.Action(if (queueHandler.hasNextItem()) R.drawable.ic_next else R.drawable.ic_next_inactive, "Next", nextIntent))

        return playIndex
    }

    private fun setNotificationPlaybackState(builder: NotificationCompat.Builder) {
        Log.d(TAG, "updateNotificationPlaybackState. playbackState=" + playbackState!!)
        if (playbackState == null || !started) {
            Log.d(TAG, "updateNotificationPlaybackState. cancelling notification!")
            audioService.stopForeground(true)
            return
        }

        builder.setOngoing(playbackState?.state == PlaybackStateCompat.STATE_PLAYING)
    }


    // Creates Notification Channel. This is required in Android O+ to display notifications.
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            val notificationChannel = NotificationChannel(CHANNEL_ID,
                    "channel id",
                    NotificationManager.IMPORTANCE_LOW)

            notificationChannel.description = "channel description"
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private val TAG = MediaNotificationManager::class.java.simpleName

        private val CHANNEL_ID = "se.asapehrsson.podtest.AUDIO_CHANNEL_ID"

        private val NOTIFICATION_ID = 412
        private val REQUEST_CODE = 100

        val ACTION_PAUSE = "se.asapehrsson.podtest.pause"
        val ACTION_PLAY = "se.asapehrsson.podtest.play"
        val ACTION_PREV = "se.asapehrsson.podtest.prev"
        val ACTION_NEXT = "se.asapehrsson.podtest.next"
        val ACTION_STOP = "se.asapehrsson.podtest.stop"
    }
}
