package se.asapehrsson.podtest.miniplayer

import se.asapehrsson.podtest.data.Episode

interface PlayerContract {
    interface View {
        fun setFirstRow(text: String?)

        fun setSecondRow(text: String?)

        fun setThumbnail(url: String?)

        fun setIconState(state: State)

        var presenter: Presenter?
    }

    interface Presenter {
        fun update(episode: Episode, view: View)

        fun start()

        fun itemClicked()

        fun close()
    }

    enum class State {
        PLAYING,
        PAUSED,
        BUFFERING,
        ENDED,
        ERROR
    }
}