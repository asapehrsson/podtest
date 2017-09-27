package se.asapehrsson.podtest.player

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log


class SimpleMediaPlayer(private val context: Context) : IMediaPlayer {
    private var mediaPlayer: MediaPlayer? = null


    override fun loadAndPlay(url: String) {
        release()
        mediaPlayer = MediaPlayer()
        initMediaPlayer(url)
    }

    override fun play() {
        mediaPlayer?.start()
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false

    private fun initMediaPlayer(url: String) {
        mediaPlayer?.let {
            it.setAudioStreamType(AudioManager.STREAM_MUSIC);
            it.setOnCompletionListener {
                //CLOSE
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
            it.setDataSource(context, Uri.parse(url))
            it.prepare()
            it.start()
        }
    }
}
