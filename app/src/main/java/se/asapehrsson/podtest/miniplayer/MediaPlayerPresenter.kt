package se.asapehrsson.podtest.miniplayer

import android.app.Activity
import android.content.ComponentName
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import se.asapehrsson.podtest.backgroundservice.BackgroundAudioService
import se.asapehrsson.podtest.data.Episode


class MediaPlayerPresenter(private val activity: Activity) : PlayerContract.Presenter {

    private val TAG = MediaPlayerPresenter::class.java.simpleName

    private val STATE_PAUSED = 0
    private val STATE_PLAYING = 1

    private var currentState: Int = 0
    private var connected = false;

    private var view: PlayerContract.View? = null

    private var episode: Episode? = null

    private var mediaBrowser: MediaBrowserCompat? = null
    private var mediaController: MediaControllerCompat? = null
    //private var mediaSession : MediaSessionCompat? = null

    private val mediaControllerCallback = object : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            if (state == null) {
                return
            }
            Log.d(TAG, "state=" + state)
            when (state.state) {
                PlaybackStateCompat.STATE_PLAYING -> {
                    currentState = STATE_PLAYING
                    view?.setIconState(PlayerContract.State.PLAYING)
                }
                PlaybackStateCompat.STATE_PAUSED -> {
                    currentState = STATE_PAUSED
                    view?.setIconState(PlayerContract.State.PAUSED)
                }
            }
        }
    }
    private val mediaBrowserConnectionCallback = object : MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            super.onConnected()
            try {

                val token = mediaBrowser?.sessionToken

                mediaController = MediaControllerCompat(activity, token!!)

                mediaController?.registerCallback(mediaControllerCallback)
                MediaControllerCompat.setMediaController(activity, mediaController)
                connected = true;

                Log.d(TAG, "")

            } catch (e: RemoteException) {

            }
        }
    }

    override fun init() {
        mediaBrowser = MediaBrowserCompat(activity, ComponentName(activity, BackgroundAudioService::class.java), mediaBrowserConnectionCallback, null)
        mediaBrowser?.connect()
    }

    override fun update(episode: Episode, view: PlayerContract.View) {
        this.view = view
        this.episode = episode

        with(view) {
            setFirstRow(episode.title)
            setSecondRow(episode.description)
            setThumbnail(episode.imageurl)
            setIconState(PlayerContract.State.PAUSED)
        }
    }

    override fun start() {
        try {
            if (connected) {
                //mediaControllerCompat?.transportControls?.playFromMediaId(episode!!.listenpodfile!!.url!!, null)
                episode?.let {

                    val description = MediaDescriptionCompat.Builder()
                            .setMediaId(episode?.listenpodfile?.url ?: "")
                            .setTitle(episode?.title)
                            .setSubtitle(episode?.description)
                            .build()
                    mediaController?.addQueueItem(description)


                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun itemClicked() {
        val mediaController = MediaControllerCompat.getMediaController(activity)
        if (currentState == STATE_PAUSED) {
            mediaController.transportControls.play()
            currentState = STATE_PLAYING
        } else {
            if (mediaController.playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
                mediaController.transportControls.pause()
            }

            currentState = STATE_PAUSED
        }
    }

    override fun close() {
        val mediaController = MediaControllerCompat.getMediaController(activity)

        if (mediaController.playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
            mediaController.transportControls.pause()
        }

        mediaBrowser?.disconnect()
    }

}