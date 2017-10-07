package se.asapehrsson.podtest.backgroundservice

import android.app.PendingIntent
import android.content.*
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import android.util.Log
import se.asapehrsson.podtest.R
import se.asapehrsson.podtest.player.ExoMediaPlayer
import se.asapehrsson.podtest.player.IMediaPlayer
import java.io.IOException

class AudioService : MediaBrowserServiceCompat(), AudioManager.OnAudioFocusChangeListener {
    private var mediaPlayer: IMediaPlayer? = null
    private var mediaSession: MediaSessionCompat? = null

    private val noisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (mediaPlayer?.isPlaying() == true) {
                mediaPlayer?.pause()
            }
        }
    }
    private var mediaNotificationManager: MediaNotificationManager? = null
    private val mediaQueueHandler = MediaQueueHandler()
    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPlayFromSearch(query: String?, extras: Bundle?) {
            super.onPlayFromSearch(query, extras)
        }

        override fun onAddQueueItem(description: MediaDescriptionCompat?) {
            mediaQueueHandler.addItem(description!!)
        }

        override fun onStop() {
            setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED)

            mediaPlayer?.release()
            mediaPlayer = null
        }

        override fun onPlay() {
            if (!successfullyRetrievedAudioFocus()) {
                return
            }

            mediaNotificationManager?.startNotification()
            mediaPlayer?.play()
        }

        override fun onPause() {
            if (mediaPlayer?.isPlaying() == true) {
                mediaPlayer?.pause()
                setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
            }
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            super.onPlayFromMediaId(mediaId, extras)
            //NOT USED
        }

        override fun onCommand(command: String?, extras: Bundle?, cb: ResultReceiver?) {
            if (COMMAND_EXAMPLE.equals(command!!, ignoreCase = true)) {
                //Custom command here
            }
        }

        override fun onSeekTo(pos: Long) {
            mediaPlayer?.seekTo(pos)
        }

        override fun onSkipToQueueItem(id: Long) {
            try {
                mediaQueueHandler.setActiveItemById(id)
                val activeQueueItem = mediaQueueHandler.getActiveItem()
                setMediaPlaybackState(PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM)
                loadAndPlay(activeQueueItem!!.description)
            } catch (e: Exception) {
                Log.d(TAG, "", e)
            }
        }

        override fun onSkipToNext() {
            if (mediaQueueHandler.hasNextItem()) {
                val activeQueueItem = mediaQueueHandler.skipToNextItem()
                setMediaPlaybackState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT)
                loadAndPlay(activeQueueItem!!.description)
            }
        }

        override fun onSkipToPrevious() {
            if (mediaQueueHandler.hasPreviousItem()) {
                val activeQueueItem = mediaQueueHandler.skipToPreviousItem()
                setMediaPlaybackState(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS)
                loadAndPlay(activeQueueItem!!.description)
            }
        }
    }

    private fun loadAndPlay(mediaDescription: MediaDescriptionCompat) {
        try {
            setMediaSessionMetadata(mediaDescription)
            setMediaPlaybackState(PlaybackStateCompat.STATE_BUFFERING)
            mediaPlayer?.loadAndPlay(mediaDescription.mediaUri!!.toString())

            if (mediaSession?.isActive == false) {
                mediaSession?.isActive = true
            }

        } catch (e: IOException) {
            Log.d(TAG, "Got exception: " + e.message)
        }
    }

    override fun onCreate() {
        super.onCreate()

        initMediaPlayer()
        initMediaSession()
        initNoisyReceiver()
        mediaNotificationManager = MediaNotificationManager(this, mediaQueueHandler)
    }

    private fun initMediaPlayer() {
        mediaPlayer = ExoMediaPlayer(this.applicationContext)
        mediaPlayer?.getMediaState()?.subscribe { state ->
            when (state) {
                PlaybackStateCompat.STATE_STOPPED -> {
                    if (mediaQueueHandler.hasNextItem()) {
                        mediaSessionCallback.onSkipToNext()
                    } else {
                        setMediaPlaybackState(state)
                    }
                }
                else -> {
                    setMediaPlaybackState(state)
                }
            }
        }
    }

    //Currently not used
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): MediaBrowserServiceCompat.BrowserRoot? {
        return if (TextUtils.equals(clientPackageName, packageName)) {
            MediaBrowserServiceCompat.BrowserRoot(getString(R.string.app_name), null)
        } else null

    }

    //Currently not used
    override fun onLoadChildren(parentId: String, result: MediaBrowserServiceCompat.Result<List<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(null)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                mediaPlayer?.let {
                    if (it.isPlaying()) {
                        it.release()
                    }
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                mediaPlayer?.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                mediaPlayer?.setVolume(0.3f)
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                mediaPlayer?.let {
                    if (!it.isPlaying()) {
                        it.play()
                    }
                    it.setVolume(1.0f)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.abandonAudioFocus(this)
        unregisterReceiver(noisyReceiver)
        mediaSession?.release()

        mediaNotificationManager?.stopNotification()
    }

    private fun initNoisyReceiver() {
        //Handles headphones coming unplugged. TODO check why this cannot be done through a manifest receiver
        val filter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(noisyReceiver, filter)
    }

    private fun initMediaSession() {
        val mediaButtonReceiver = ComponentName(applicationContext, MediaButtonReceiver::class.java)
        mediaSession = MediaSessionCompat(applicationContext, MEDIA_SESSION_ID, mediaButtonReceiver, null)
        mediaSession?.let {
            it.setCallback(mediaSessionCallback)
            it.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS or MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS)
            it.setQueue(mediaQueueHandler.mediaQueue)
            sessionToken = it.sessionToken

            val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
            mediaButtonIntent.setClass(this, MediaButtonReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0)
            it.setMediaButtonReceiver(pendingIntent)
        }

    }

    private fun setMediaSessionMetadata(mediaDescription: MediaDescriptionCompat) {
        val metadataBuilder = MediaMetadataCompat.Builder()
        //Notification icon in card
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))

        //lock screen icon for pre lollipop
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, mediaDescription.title!!.toString())
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, mediaDescription.subtitle!!.toString())
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ART_URI, mediaDescription.iconUri!!.toString())

        val duration = mediaDescription.extras?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0) ?: 0
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, (mediaQueueHandler.activeQueueIndex + 1).toLong())
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, (mediaQueueHandler.mediaQueue.size + 1).toLong())

        mediaSession!!.setMetadata(metadataBuilder.build())
    }

    private fun setMediaPlaybackState(state: Int) {
        var capabilities = PlaybackStateCompat.ACTION_PLAY_PAUSE
        val playbackstateBuilder = PlaybackStateCompat.Builder()
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            capabilities = capabilities or PlaybackStateCompat.ACTION_PAUSE
        } else {
            capabilities = capabilities or PlaybackStateCompat.ACTION_PLAY
        }

        if (mediaQueueHandler.hasPreviousItem()) {
            capabilities = capabilities or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        }
        if (mediaQueueHandler.hasNextItem()) {
            capabilities = capabilities or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        }
        playbackstateBuilder.setActions(capabilities)

        var position = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN
        var playbackSpeed = 0

        mediaPlayer?.let {
            position = it.currentPositionInMillis()
            playbackSpeed = if (it.isPlaying()) 1 else 0
        }

        playbackstateBuilder.setState(state, position, playbackSpeed.toFloat())

        mediaSession?.setPlaybackState(playbackstateBuilder.build())
    }


    private fun successfullyRetrievedAudioFocus(): Boolean {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)

        return result == AudioManager.AUDIOFOCUS_GAIN
    }

    companion object {
        val COMMAND_EXAMPLE = "command_example"
        val MEDIA_SESSION_ID = "Tag"
        private val TAG = AudioService::class.java.simpleName
    }

}
