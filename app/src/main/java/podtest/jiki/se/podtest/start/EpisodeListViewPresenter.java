package podtest.jiki.se.podtest.start;

import podtest.jiki.se.podtest.R;
import podtest.jiki.se.podtest.model.Episode;

public class EpisodeListViewPresenter implements EpisodeContract.Presenter {
    private EpisodeViewer episodeViewer;

    public EpisodeListViewPresenter(EpisodeViewer episodeViewer) {
        this.episodeViewer = episodeViewer;
    }

    @Override
    public void update(Episode episode, EpisodeContract.View view) {
        view.setFirstRow(episode.getTitle());
        view.setSecondRow(episode.getDescription());
        view.setThumbnail(episode.getImageurl());
        view.setIcon(R.drawable.ic_info);
        view.setTag(episode);
    }

    @Override
    public void itemClicked(Object tag, @EpisodeContract.Source int source) {
        if (source == EpisodeContract.Source.ICON_IMAGE) {
            episodeViewer.showInfo((Episode) tag);
        } else {
            episodeViewer.play((Episode) tag);
        }
    }
}
