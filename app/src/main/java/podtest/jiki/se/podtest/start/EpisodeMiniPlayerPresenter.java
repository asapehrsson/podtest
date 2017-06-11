package podtest.jiki.se.podtest.start;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import podtest.jiki.se.podtest.R;
import podtest.jiki.se.podtest.model.Episode;

public class EpisodeMiniPlayerPresenter implements RowItemContract.EpisodePresenter {
    private RowItemContract.EpisodeView view;
    private MediaPlayer mediaPlayer;
    private Context context;

    public EpisodeMiniPlayerPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void update(Episode episode, RowItemContract.EpisodeView view) {
        this.view = view;
        view.setFirstRow(episode.getTitle());
        view.setSecondRow(episode.getDescription());
        view.setThumbnail(episode.getImageurl());
        view.setRightIcon(R.drawable.ic_pause);

        try {
            mediaPlayer = MediaPlayer.create(context, Uri.parse(episode.getListenpodfile().getUrl()));
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void itemClicked(Object tag, @RowItemContract.Source int source) {
        if (source == RowItemContract.Source.RIGHT_ICON) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                view.setRightIcon(R.drawable.ic_play);
            } else {
                mediaPlayer.start();
                view.setRightIcon(R.drawable.ic_pause);
            }
        }
    }

    void close() {
        mediaPlayer.release();
    }
}
