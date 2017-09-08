package se.asapehrsson.podtest

import se.asapehrsson.podtest.model.Episode

class EpisodeListViewPresenter(private val episodeViewer: EpisodeViewer) : EpisodeContract.Presenter {
    override fun close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(episode: Episode?, view: EpisodeContract.View?) {
        if (episode != null) {
            view?.setFirstRow(episode.title)
            view?.setSecondRow(episode.description)
            view?.setThumbnail(episode.imageurl)
            view?.setIcon(R.drawable.ic_info)
            view?.setTag(episode)
        }
    }

    override fun itemClicked(tag: Any, source: EpisodeContract.Source) {
        if (source == EpisodeContract.Source.ICON_IMAGE) {
            episodeViewer.showInfo(tag as Episode)
        } else {
            episodeViewer.play(tag as Episode)
        }
    }
}
