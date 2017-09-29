package se.asapehrsson.podtest.miniplayer

import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import se.asapehrsson.podtest.backgroundservice.AudioService
import se.asapehrsson.podtest.data.Episode


class MediaPlayerPresenter : PlayerContract.Presenter {

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
                PlaybackStateCompat.STATE_NONE -> {
                    mediaController?.metadata?.let {
                        var desc = it.description
                        view!!.setFirstRow(desc?.title?.toString())
                        view!!.setSecondRow(desc?.subtitle?.toString())
                        view!!.setThumbnail(desc?.iconUri?.toString())
                        view!!.setIconState(PlayerContract.State.PAUSED)
                    }
                }
            }

            var rev = state.actions and PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS > 0
            var ff = state.actions and PlaybackStateCompat.ACTION_SKIP_TO_NEXT > 0

            view?.setSkipIcons(rev, ff)
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
    }

    override fun play(id: Int) {
        try {
            episode?.let {
                mediaController?.transportControls?.skipToQueueItem(id.toLong())
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
            PlayerContract.Source.SEEK_DONE -> {
                val newPosition = if (arg == 0) 0 else episode?.listenpodfile?.duration!! * arg / 100
                mediaController?.transportControls?.seekTo(newPosition.toLong())
                mediaController?.transportControls?.play()
            }
            PlayerContract.Source.CLOSE -> mediaController?.transportControls?.stop()
            PlayerContract.Source.NEXT -> mediaController?.transportControls?.skipToNext()
            PlayerContract.Source.PREV -> mediaController?.transportControls?.skipToPrevious()
        }
    }

    override fun close() {
        mediaController?.unregisterCallback(mediaControllerCallback)
    }
}