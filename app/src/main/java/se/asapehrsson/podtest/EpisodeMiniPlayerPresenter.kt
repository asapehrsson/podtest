package se.asapehrsson.podtest

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri

import se.asapehrsson.podtest.model.Episode

class EpisodeMiniPlayerPresenter(private val context: Context) : EpisodeContract.Presenter {

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
            mediaPlayer = MediaPlayer.create(context, Uri.parse(episode!!.listenpodfile.url))
            mediaPlayer!!.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun itemClicked(tag: Any, source: EpisodeContract.Source) {
        if (source == EpisodeContract.Source.ICON_IMAGE && mediaPlayer != null) {
            val playing = mediaPlayer?.isPlaying() ?: false

            if (playing) {
                mediaPlayer?.pause()
                view?.setIcon(R.drawable.ic_play)
            } else {
                mediaPlayer?.start()
                view?.setIcon(R.drawable.ic_pause)
            }
        }
    }

    override fun close() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    val isPlayerActive: Boolean
        get() = mediaPlayer != null
}
