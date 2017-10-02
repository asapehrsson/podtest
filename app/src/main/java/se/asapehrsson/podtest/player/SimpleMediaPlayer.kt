package se.asapehrsson.podtest.player

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.PowerManager
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import io.reactivex.subjects.BehaviorSubject
import java.io.IOException


class SimpleMediaPlayer(private val context: Context) : IMediaPlayer {
    override fun currentPositionInMillis(): Long {
        return mediaPlayer?.currentPosition?.toLong() ?: 0
    }

    override fun seekTo(positionInMillis: Long) {
        mediaPlayer?.seekTo(positionInMillis.toInt())
    }

    override fun setVolume(fl: Float) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var mediaPlayer: MediaPlayer? = null
    private var behaviorSubject = BehaviorSubject.create<@PlaybackStateCompat.State Int>()

    override fun getMediaState(): BehaviorSubject<Int>? {
        return behaviorSubject;
    }

    override fun play() {
        mediaPlayer?.start()
        behaviorSubject.onNext(PlaybackStateCompat.STATE_PLAYING)
    }

    override fun pause() {
        mediaPlayer?.pause()
        behaviorSubject.onNext(PlaybackStateCompat.STATE_PAUSED)
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false


    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.let {
            it.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                it.setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
            } else {
                it.setAudioStreamType(AudioManager.STREAM_MUSIC)
            }
            it.setVolume(1.0f, 1.0f)

            it.setOnCompletionListener { mediaPlayer ->
                if (mediaPlayer.duration - mediaPlayer.currentPosition < 900) {
                    behaviorSubject.onNext(PlaybackStateCompat.STATE_STOPPED)
                }
            }

            it.setOnPreparedListener {
                Log.d("", "")
            }
            it.setOnSeekCompleteListener {
                Log.d("", "")
            }
            it.setOnInfoListener(object : MediaPlayer.OnInfoListener {
                override fun onInfo(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
                    Log.d("", "")
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
        }
    }

    override fun loadAndPlay(url: String) {
        try {
            if (mediaPlayer == null) {
                initMediaPlayer()
            } else {
                mediaPlayer?.reset()
            }
            behaviorSubject.onNext(PlaybackStateCompat.STATE_NONE)
            mediaPlayer?.setDataSource(url)

            mediaPlayer?.prepare()
            mediaPlayer?.start()

            behaviorSubject.onNext(PlaybackStateCompat.STATE_PLAYING)

        } catch (e: IOException) {
            Log.d(TAG, "Got exception: " + e.message)
        }
    }

    companion object {
        private val TAG = SimpleMediaPlayer::class.java.simpleName
    }
}
