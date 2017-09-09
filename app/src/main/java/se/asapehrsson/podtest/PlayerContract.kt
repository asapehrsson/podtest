package se.asapehrsson.podtest

import se.asapehrsson.podtest.data.Episode

interface PlayerContract {
    interface View {
        fun setFirstRow(text: String?)

        fun setSecondRow(text: String?)

        fun setThumbnail(url: String?)

        fun setIcon(drawableResourceId: Int)

        fun setTag(tag: Any)

        fun setPresenter(presenter: PlayerContract.Presenter)
    }

    interface Presenter {
        fun update(episode: Episode?, view: View?)

        fun itemClicked(tag: Any, request: Request)

        fun close();
    }

    enum class Request {
        PLAY_EPISODE,
        SHOW_DETAILS
    }
}