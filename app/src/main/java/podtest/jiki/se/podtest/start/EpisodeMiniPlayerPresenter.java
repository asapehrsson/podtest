package podtest.jiki.se.podtest.start;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import podtest.jiki.se.podtest.R;
import podtest.jiki.se.podtest.model.Episode;

public class EpisodeMiniPlayerPresenter implements EpisodeContract.Presenter {
    private EpisodeContract.View view;
    private MediaPlayer mediaPlayer;
    private Context context;

    public EpisodeMiniPlayerPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void update(Episode episode, EpisodeContract.View view) {
        this.view = view;
        view.setFirstRow(episode.getTitle());
        view.setSecondRow(episode.getDescription());
        view.setThumbnail(episode.getImageurl());
        view.setIcon(R.drawable.ic_pause);

        try {
            mediaPlayer = MediaPlayer.create(context, Uri.parse(episode.getListenpodfile().getUrl()));
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void itemClicked(Object tag, @EpisodeContract.Source int source) {
        if (source == EpisodeContract.Source.ICON_IMAGE && mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                view.setIcon(R.drawable.ic_play);
            } else {
                mediaPlayer.start();
                view.setIcon(R.drawable.ic_pause);
            }
        }
    }

    public void close() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public boolean isPlayerActive() {
        return mediaPlayer != null;
    }
}
