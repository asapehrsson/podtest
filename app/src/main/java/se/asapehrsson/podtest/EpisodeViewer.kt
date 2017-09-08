package se.asapehrsson.podtest


import se.asapehrsson.podtest.model.Episode

interface EpisodeViewer {
    fun showInfo(episode: Episode)

    fun play(episode: Episode)
}
