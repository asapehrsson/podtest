package se.asapehrsson.podtest.backgroundservice

import android.app.PendingIntent
import android.content.*
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
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
import java.io.IOException
import java.util.*

class AudioService : MediaBrowserServiceCompat(), AudioManager.OnAudioFocusChangeListener {
    private var mediaPlayer: MediaPlayer? = null
    private var mediaSession: MediaSessionCompat? = null
    private var currentQueueIndex = -1

    private val noisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        }
    }
    private val mediaQueue = ArrayList<MediaSessionCompat.QueueItem>()
    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPrepare() {
            super.onPrepare()
        }

        override fun onAddQueueItem(description: MediaDescriptionCompat?) {
            super.onAddQueueItem(description)
            mediaQueue.add(MediaSessionCompat.QueueItem(description!!, Integer.parseInt(description.mediaId).toLong()))
        }

        override fun onStop() {
            super.onStop()
            setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED)

            mediaPlayer?.release()
            mediaPlayer = null
        }

        override fun onPlay() {
            super.onPlay()
            if (!successfullyRetrievedAudioFocus()) {
                return
            }

            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING)
            NotificationHelper.showPlayingNotification(this@AudioService, mediaSession!!, validIndex(currentQueueIndex - 1), validIndex(currentQueueIndex + 1))
            mediaPlayer?.start()
        }

        override fun onPause() {
            super.onPause()

            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
                NotificationHelper.showPausedNotification(mediaSession!!, this@AudioService, validIndex(currentQueueIndex - 1), validIndex(currentQueueIndex + 1))
            }
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            super.onPlayFromMediaId(mediaId, extras)
            //NOT USED
        }

        override fun onCommand(command: String?, extras: Bundle?, cb: ResultReceiver?) {
            super.onCommand(command, extras, cb)
            if (COMMAND_EXAMPLE.equals(command!!, ignoreCase = true)) {
                //Custom command here
            }
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            mediaPlayer?.seekTo(pos.toInt())
        }

        override fun onSkipToQueueItem(id: Long) {
            super.onSkipToQueueItem(id)
            for (i in mediaQueue.indices) {
                val queueItem = mediaQueue[i]
                if (queueItem.queueId == id) {
                    currentQueueIndex = i
                    setMediaPlaybackState(PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM)
                    loadAndPlay(queueItem.description)
                }
            }
        }

        override fun onSkipToNext() {
            super.onSkipToNext()

            val newIndex = currentQueueIndex + 1
            if (validIndex(newIndex)) {
                currentQueueIndex = newIndex
                setMediaPlaybackState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT)
                loadAndPlay(mediaQueue[newIndex].description)
            }
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            val newIndex = currentQueueIndex - 1
            if (validIndex(newIndex)) {
                currentQueueIndex = newIndex
                setMediaPlaybackState(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS)
                loadAndPlay(mediaQueue[newIndex].description)
            }
        }
    }

    private fun validIndex(index: Int): Boolean = index >= 0 && index < mediaQueue.size

    private fun loadAndPlay(mediaDescription: MediaDescriptionCompat) {
        try {
            if (mediaPlayer == null) {
                initMediaPlayer()
            } else {
                mediaPlayer?.reset()
            }

            mediaPlayer?.setDataSource(applicationContext, mediaDescription.mediaUri!!)

            setMediaSessionMetadata(mediaDescription)
            setMediaPlaybackState(PlaybackStateCompat.STATE_NONE)

            mediaPlayer?.prepare()
            mediaPlayer?.start()
            if (mediaSession?.isActive == false) {
                mediaSession?.isActive = true
            }
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING)
            NotificationHelper.showPlayingNotification(this@AudioService, mediaSession!!, validIndex(currentQueueIndex - 1), validIndex(currentQueueIndex + 1))

        } catch (e: IOException) {
            Log.d(TAG, "Got exception: " + e.message)
        }

    }

    override fun onCreate() {
        super.onCreate()

        initMediaPlayer()
        initMediaSession()
        initNoisyReceiver()
    }


    //Not important for general audio service, required for class
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): MediaBrowserServiceCompat.BrowserRoot? {
        return if (TextUtils.equals(clientPackageName, packageName)) {
            MediaBrowserServiceCompat.BrowserRoot(getString(R.string.app_name), null)
        } else null

    }

    //Not important for general audio service, required for class
    override fun onLoadChildren(parentId: String, result: MediaBrowserServiceCompat.Result<List<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(null)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        it.stop()
                    }
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                mediaPlayer?.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                mediaPlayer?.setVolume(0.3f, 0.3f)
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                mediaPlayer?.let {
                    if (!it.isPlaying) {
                        it.start()
                    }
                    it.setVolume(1.0f, 1.0f)
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
        mediaSession!!.release()
        NotificationManagerCompat.from(this).cancel(1)
    }

    private fun initNoisyReceiver() {
        //Handles headphones coming unplugged. cannot be done through a manifest receiver
        val filter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(noisyReceiver, filter)
    }


    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()

        mediaPlayer?.let {
            it.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                it.setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
            } else {
                it.setAudioStreamType(AudioManager.STREAM_MUSIC)
            }
            it.setVolume(1.0f, 1.0f)

            it.setOnCompletionListener { mediaPlayer ->
                if (mediaPlayer.duration - mediaPlayer.currentPosition < 10) {
                    mediaSessionCallback.onSkipToNext()
                }
            }
        }
    }

    private fun initMediaSession() {
        val mediaButtonReceiver = ComponentName(applicationContext, MediaButtonReceiver::class.java)
        mediaSession = MediaSessionCompat(applicationContext, MEDIA_SESSION_ID, mediaButtonReceiver, null)
        mediaSession?.let {
            it.setCallback(mediaSessionCallback)
            it.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS or MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS)
            it.setQueue(mediaQueue)
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
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, (currentQueueIndex + 1).toLong())
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, (mediaQueue.size + 1).toLong())

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

        if (validIndex(currentQueueIndex - 1)) {
            capabilities = capabilities or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        }
        if (validIndex(currentQueueIndex + 1)) {
            capabilities = capabilities or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        }
        playbackstateBuilder.setActions(capabilities)

        var position = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN
        var playbackSpeed = 0

        mediaPlayer?.let {
            position = it.currentPosition.toLong()
            playbackSpeed = if (it.isPlaying) 1 else 0
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
