package se.asapehrsson.podtest

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import org.jetbrains.anko.runOnUiThread

import se.asapehrsson.podtest.data.Episode

class EpisodeMiniPlayerPresenter(private val context: Context) : EpisodeContract.Presenter, MediaPlayer.OnInfoListener {
    override fun onInfo(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var view: EpisodeContract.View? = null
    private var mediaPlayer: MediaPlayer? = null
    private var episode: Episode? = null

    override fun update(episode: Episode?, view: EpisodeContract.View?) {
        this.view = view
        this.episode = episode
        if (episode != null && view != null) {
            view.setFirstRow(episode.title)
            view.setSecondRow(episode.description)
            view.setThumbnail(episode.imageurl)
            view.setIcon(R.drawable.ic_pause)
        }
    }

    fun startPlayer() {
        try {
            close()
            setIcon()
            mediaPlayer = MediaPlayer();
            mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer!!.setOnCompletionListener {
                //CLOSE
            }
            mediaPlayer!!.setOnPreparedListener {
                setIcon()
                Log.d("", "")
            }
            mediaPlayer!!.setOnSeekCompleteListener {
                Log.d("", "")
            }
            mediaPlayer!!.setOnInfoListener(object : MediaPlayer.OnInfoListener {
                override fun onInfo(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
                    Log.d("", "")
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
            mediaPlayer!!.setDataSource(context, Uri.parse(episode!!.listenpodfile!!.url));
            mediaPlayer!!.prepare();
            mediaPlayer!!.start();


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun setIcon() {
        var isActive: Boolean = mediaPlayer != null
        context.runOnUiThread {
            if (isActive) {
                val playing = mediaPlayer?.isPlaying() ?: false
                if (playing) {
                    view?.setIcon(R.drawable.ic_play)
                } else {
                    view?.setIcon(R.drawable.ic_pause)
                }

            } else {
                view?.setIcon(0)
            }
        }

    }

    override fun itemClicked(tag: Any, source: EpisodeContract.Source) {
        if (source == EpisodeContract.Source.ICON_IMAGE && mediaPlayer != null) {
            val playing = mediaPlayer?.isPlaying() ?: false

            if (playing) {
                mediaPlayer?.pause()
            } else {
                mediaPlayer?.start()
            }

            setIcon()
        }
    }

    override fun close() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    val isPlayerActive: Boolean
        get() = mediaPlayer != null
}
