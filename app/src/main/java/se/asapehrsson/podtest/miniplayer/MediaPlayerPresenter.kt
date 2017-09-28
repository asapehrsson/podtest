package se.asapehrsson.podtest.miniplayer

import android.media.MediaMetadata
import android.support.v4.media.MediaMetadataCompat
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
                PlaybackStateCompat.STATE_NONE ->{
                    mediaController?.metadata?.let {
                        var desc = it.description
                        view!!.setFirstRow(desc?.title?.toString())
                        view!!.setSecondRow(desc?.subtitle?.toString())
                        view!!.setThumbnail(desc?.iconUri?.toString())
                        view!!.setIconState(PlayerContract.State.PAUSED)
                    }
                }
            }

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

//        with(view) {
//            setFirstRow(episode.title)
//            setSecondRow(episode.description)
//            setThumbnail(episode.imageurl)
//            setIconState(PlayerContract.State.PAUSED)
//        }
    }

    override fun play(id: Int) {
        try {
            episode?.let {
                mediaController?.transportControls?.skipToQueueItem(1234L)
                mediaController?.sendCommand(AudioService.COMMAND_EXAMPLE, null, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun event(source: PlayerContract.Source, arg: Int) {
        when (source) {
            PlayerContract.Source.PLAY_PAUSE -> {
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
            PlayerContract.Source.SEEK_START -> mediaController?.transportControls?.pause()
            PlayerContract.Source.SEEK_DONE -> mediaController?.transportControls?.play()
            PlayerContract.Source.CLOSE -> mediaController?.transportControls?.stop()
        }
    }

    override fun close() {
        mediaController?.unregisterCallback(mediaControllerCallback)
    }
}