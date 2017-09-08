package se.asapehrsson.podtest

import se.asapehrsson.podtest.model.Episode


interface EpisodeContract {
    interface View {
        fun setFirstRow(text: String)

        fun setSecondRow(text: String)

        fun setThumbnail(url: String)

        fun setIcon(drawableResourceId: Int)

        fun setTag(tag: Any)

        fun setPresenter(presenter: EpisodeContract.Presenter)
    }

    interface Presenter {
        fun update(episode: Episode?, view: View?)

        fun itemClicked(tag: Any, source: Source)

        fun close();
    }

    enum class Source {
        CONTAINER,
        ICON_IMAGE
    }
}
