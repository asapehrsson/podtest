package podtest.jiki.se.podtest.start;

import podtest.jiki.se.podtest.R;
import podtest.jiki.se.podtest.model.Episode;

public class EpisodeListViewPresenter implements RowItemContract.EpisodePresenter {
    private EpisodeViewer episodeViewer;

    public EpisodeListViewPresenter(EpisodeViewer episodeViewer) {
        this.episodeViewer = episodeViewer;
    }

    @Override
    public void update(Episode episode, RowItemContract.EpisodeView view) {
        view.setFirstRow(episode.getTitle());
        view.setSecondRow(episode.getDescription());
        view.setThumbnail(episode.getImageurl());
        view.setRightIcon(R.drawable.ic_info);
        view.setTag(episode);
    }

    @Override
    public void itemClicked(Object tag, @RowItemContract.Source int source) {
        if (source == RowItemContract.Source.RIGHT_ICON) {
            episodeViewer.showInfo((Episode) tag);
        } else {
            episodeViewer.play((Episode) tag);
        }
    }
}
