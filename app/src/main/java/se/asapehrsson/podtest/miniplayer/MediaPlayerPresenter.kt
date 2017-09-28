package se.asapehrsson.podtest.miniplayer

import android.net.Uri
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import se.asapehrsson.podtest.backgroundservice.AudioService
import se.asapehrsson.podtest.data.Episode


class MediaPlayerPresenter() : PlayerContract.Presenter {

    private val TAG = MediaPlayerPresenter::class.java.simpleName

    private val STATE_PAUSED = 0
    private val STATE_PLAYING = 1

    private var currentState: Int = 0

    private var view: PlayerContract.View? = null

    private var episode: Episode? = null

    private var mediaController: MediaControllerCompat? = null

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

            var r = mediaController?.queue
            Log.d("", "")
        }
    }


    fun init(mediaController: MediaControllerCompat) {
        this.mediaController = mediaController;
        mediaController.unregisterCallback(mediaControllerCallback)
        mediaController.registerCallback(mediaControllerCallback)
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
            episode?.let {

                val description = MediaDescriptionCompat.Builder()

                        .setMediaId(episode?.listenpodfile?.url ?: "")
                        .setTitle(episode?.title)
                        .setSubtitle(episode?.description)
                        .setIconUri(Uri.parse(episode?.imageurl))
                        //.setMediaUri()
                        .build()
                mediaController?.addQueueItem(description)
                //mediaController?.transportControls?.play()

                mediaController?.transportControls?.skipToQueueItem(1234L)
                mediaController?.sendCommand(AudioService.COMMAND_EXAMPLE, null, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun itemClicked() {
        if (currentState == STATE_PAUSED) {
            mediaController?.transportControls?.play()
            currentState = STATE_PLAYING
        } else {
            if (mediaController?.playbackState?.state == PlaybackStateCompat.STATE_PLAYING) {
                mediaController?.transportControls?.pause()
            }

            currentState = STATE_PAUSED
        }
    }

    override fun close() {
        mediaController?.unregisterCallback(mediaControllerCallback)
    }
}