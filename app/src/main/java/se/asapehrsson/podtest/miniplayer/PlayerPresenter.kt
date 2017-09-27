package se.asapehrsson.podtest.miniplayer

import android.content.Context
import android.media.MediaPlayer
import org.jetbrains.anko.runOnUiThread
import se.asapehrsson.podtest.data.Episode
import se.asapehrsson.podtest.player.IMediaPlayer
import se.asapehrsson.podtest.player.SimpleMediaPlayer

class PlayerPresenter(private val context: Context) : PlayerContract.Presenter, MediaPlayer.OnInfoListener {


    override fun onInfo(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var view: PlayerContract.View? = null
    private var mediaPlayer: IMediaPlayer? = null
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
            //view?.setIconState(PlayerContract.State.BUFFERING)
            mediaPlayer = SimpleMediaPlayer(context)
            (mediaPlayer as SimpleMediaPlayer).loadAndPlay(episode!!.listenpodfile!!.url!!)
            context.runOnUiThread {
                view?.setIconState(PlayerContract.State.PLAYING)
            }

        } catch (e: Exception) {
            e.printStackTrace()
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
                it.play()
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
