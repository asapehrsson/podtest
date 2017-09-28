package se.asapehrsson.podtest.miniplayer

import se.asapehrsson.podtest.data.Episode

interface PlayerContract {
    interface View {
        fun setFirstRow(text: String?)

        fun setSecondRow(text: String?)

        fun setThumbnail(url: String?)

        fun setIconState(state: State)

        fun setProgress(progress: Int, max: Int)

        var presenter: Presenter?
    }

    interface Presenter {

        fun update(episode: Episode, view: View)

        fun start()

        fun event(source: Source, arg: Int = 0)

        fun close()
    }

    enum class State {
        PLAYING,
        PAUSED,
        BUFFERING,
        ENDED,
        ERROR
    }

    enum class Source {
        PLAY_PAUSE,
        NEXT,
        PREV,
        CLOSE,
        SEEK_START,
        SEEK_DONE
    }
}