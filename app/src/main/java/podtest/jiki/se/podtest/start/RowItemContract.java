package podtest.jiki.se.podtest.start;

import podtest.jiki.se.podtest.model.Episode;

/**
 * Created by asapehrsson on 2017-06-07.
 */

public interface RowItemContract {
    interface EpisodeView {
        void setFirstRow(String text);

        void setSecondRow(String text);

        void setThumbnail(String url);

        void setTag(Object tag);

        void setPresenter(EpisodePresenter presenter);
    }
    interface EpisodePresenter {
        void update(Episode episode, EpisodeView view);

        void itemClicked(Object tag);
    }
}
