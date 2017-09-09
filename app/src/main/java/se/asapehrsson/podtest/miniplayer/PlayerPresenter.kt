package se.asapehrsson.podtest.miniplayer

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import org.jetbrains.anko.runOnUiThread

import se.asapehrsson.podtest.data.Episode

class PlayerPresenter(private val context: Context) : PlayerContract.Presenter, MediaPlayer.OnInfoListener {


    override fun onInfo(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var view: PlayerContract.View? = null
    private var mediaPlayer: MediaPlayer? = null
    private var episode: Episode? = null

    override fun update(episode: Episode, view: PlayerContract.View) {
        this.view = view
        this.episode = episode

        with(view) {
            setFirstRow(episode.title)
            setSecondRow(episode.description)
            setThumbnail(episode.imageurl)
            setIconState(PlayerContract.State.BUFFERING)
        }
    }

    override fun start() {
        try {
            close()
            view?.setIconState(PlayerContract.State.BUFFERING)
            mediaPlayer = MediaPlayer();

            initMediaPlayer()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initMediaPlayer() {
        mediaPlayer?.let {
            it.setAudioStreamType(AudioManager.STREAM_MUSIC);
            it.setOnCompletionListener {
                //CLOSE
            }
            it.setOnPreparedListener {
                setVisibleState()
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
            it.setDataSource(context, Uri.parse(episode!!.listenpodfile!!.url))
            it.prepare()
            it.start()
        }
    }

    fun setVisibleState() {
        var isActive: Boolean = mediaPlayer != null
        context.runOnUiThread {
            if (isActive) {
                val playing = mediaPlayer?.isPlaying ?: false
                if (playing) {
                    view?.setIconState(PlayerContract.State.PLAYING)
                } else {
                    view?.setIconState(PlayerContract.State.PAUSED)
                }

            } else {
                view?.setIconState(PlayerContract.State.BUFFERING)
            }
        }
    }

    override fun itemClicked() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.start()
            }
            setVisibleState()
        }
    }

    override fun close() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    val isPlayerActive: Boolean
        get() = mediaPlayer != null
}
