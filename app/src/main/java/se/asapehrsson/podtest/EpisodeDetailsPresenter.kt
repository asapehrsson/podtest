package se.asapehrsson.podtest

import se.asapehrsson.podtest.data.Episode

class EpisodeDetailsPresenter(private val view: EpisodeContract.View) {

    fun update(episode: Episode) {
        view.setFirstRow(episode.title)
        view.setSecondRow(episode.description)
    }
}
