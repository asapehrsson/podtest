package podtest.jiki.se.podtest.start;

import podtest.jiki.se.podtest.model.Episode;

public class EpisodeDetailsPresenter {
    private EpisodeContract.View view;

    public EpisodeDetailsPresenter(EpisodeContract.View view) {
        this.view = view;
    }

    public void update(Episode episode) {
        view.setFirstRow(episode.getTitle());
        view.setSecondRow(episode.getDescription());
    }
}
