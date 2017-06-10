package podtest.jiki.se.podtest.start;

import podtest.jiki.se.podtest.model.Episode;

/**
 * Created by asapehrsson on 2017-06-07.
 */

public class EpisodeViewPresenter implements RowItemContract.EpisodePresenter {
    @Override
    public void update(Episode episode, RowItemContract.EpisodeView view) {
        view.setFirstRow(episode.getTitle());
        view.setSecondRow(episode.getDescription());
        view.setThumbnail(episode.getImageurl());
    }

    @Override
    public void itemClicked(Object tag) {

    }
}
