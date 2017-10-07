package se.asapehrsson.podtest.miniplayer

import android.os.Handler
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import se.asapehrsson.podtest.backgroundservice.AudioService
import se.asapehrsson.podtest.data.Episode

//Interacting with player by using MediaControllerCompat
class MediaPlayerPresenter : PlayerContract.Presenter {

    private val TAG = MediaPlayerPresenter::class.java.simpleName

    private val STATE_PAUSED = 0
    private val STATE_PLAYING = 1
    private val STATE_IDLE = 2

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
            var mayUpdateProgress = false

            when (state.state) {
                PlaybackStateCompat.STATE_PLAYING -> {
                    currentState = STATE_PLAYING
                    view?.setIconState(PlayerContract.State.PLAYING)
                    mayUpdateProgress = true
                }
                PlaybackStateCompat.STATE_PAUSED -> {
                    currentState = STATE_PAUSED
                    view?.setIconState(PlayerContract.State.PAUSED)
                }
                PlaybackStateCompat.STATE_NONE -> {
                    handler?.removeCallbacksAndMessages(null)
                    view?.setProgress(0, 100)
                    mediaController?.metadata?.let {
                        var desc = it.description
                        view?.setFirstRow(desc?.title?.toString())
                        view?.setSecondRow(desc?.subtitle?.toString())
                        view?.setThumbnail(desc?.iconUri?.toString())
                        //view?.setIconState(PlayerContract.State.PAUSED)

                        durationInMillis = it.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
                    }
                }
                PlaybackStateCompat.STATE_SKIPPING_TO_NEXT,
                PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS,
                PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM -> {
                    view?.setIconState(PlayerContract.State.BUFFERING)
                    handler?.removeCallbacksAndMessages(null)
                }
                else -> {
                    currentState = STATE_IDLE
                }
            }

            var rev = state.actions and PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS > 0
            var ff = state.actions and PlaybackStateCompat.ACTION_SKIP_TO_NEXT > 0

            view?.setSkipIcons(rev, ff)


            var newPositionInMillis = (mediaController?.playbackState?.position) ?: 0 + (mediaController?.playbackState?.lastPositionUpdateTime ?: 0)
            var newPositionUpdateTimeInMillis = System.currentTimeMillis()

            if (mayUpdateProgress && newPositionInMillis != positionInMillis) {
                handler?.removeCallbacksAndMessages(null)
                positionUpdateTimeInMillis = newPositionUpdateTimeInMillis
                positionInMillis = newPositionInMillis
                Log.d(TAG, "event. updating progress=" + positionInMillis + " duration=" + durationInMillis)
                handler!!.post(runnable)
            }
        }
    }

    private var positionUpdateTimeInMillis: Long = 0
    private var positionInMillis: Long = 0
    private var durationInMillis: Long = 0

    private var handler: Handler? = null

    private val runnable = object : Runnable {
        override fun run() {
            var currentPos = ((System.currentTimeMillis() - positionUpdateTimeInMillis) + positionInMillis)

            if (currentState == STATE_PLAYING && currentPos <= durationInMillis) {
                view?.setProgress(currentPos.toInt(), durationInMillis.toInt())

                Log.d(TAG, "progress=" + currentPos + " duration=" + durationInMillis)
            }
            handler?.postDelayed(this, 800) // reschedule
        }
    }

    fun init(mediaController: MediaControllerCompat) {
        this.mediaController = mediaController;
        mediaController.unregisterCallback(mediaControllerCallback)
        mediaController.registerCallback(mediaControllerCallback)
        handler = Handler() // new handler
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
                Log.d(TAG, "seek to=" + arg + " duration=" + durationInMillis)
                mediaController?.transportControls?.seekTo(arg.toLong())
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