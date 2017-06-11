package podtest.jiki.se.podtest.start;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import podtest.jiki.se.podtest.model.Episode;

import static java.lang.annotation.RetentionPolicy.SOURCE;


public interface RowItemContract {
    interface EpisodeView {
        void setFirstRow(String text);

        void setSecondRow(String text);

        void setThumbnail(String url);

        void setRightIcon(int drawableResourceId);

        void setTag(Object tag);

        void setPresenter(EpisodePresenter presenter);
    }

    interface EpisodePresenter {
        void update(Episode episode, EpisodeView view);

        void itemClicked(Object tag, @Source int source);
    }

    @Retention(SOURCE)
    @IntDef({Source.RIGHT_ICON, Source.CONTAINER})
    @interface Source {
        int CONTAINER = 0;
        int RIGHT_ICON = 1;
    }
}
