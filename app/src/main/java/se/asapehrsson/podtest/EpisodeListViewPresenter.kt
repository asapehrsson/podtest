package se.asapehrsson.podtest

import se.asapehrsson.podtest.data.Episode

class EpisodeListViewPresenter(private val episodeViewer: EpisodeViewer) : EpisodeContract.Presenter {
    override fun close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(episode: Episode?, view: EpisodeContract.View?) {
        if (episode != null) {
            view?.setFirstRow(episode.title)
            view?.setSecondRow(episode.description)
            view?.setThumbnail(episode.imageurl)
            view?.setTag(episode)
        }
    }

    override fun itemClicked(tag: Any, request: EpisodeContract.Request) {
        if (request == EpisodeContract.Request.SHOW_DETAILS) {
            episodeViewer.showInfo(tag as Episode)
        } else {
            episodeViewer.play(tag as Episode)
        }
    }
}
