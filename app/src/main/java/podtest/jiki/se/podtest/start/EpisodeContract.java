package podtest.jiki.se.podtest.start;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import podtest.jiki.se.podtest.model.Episode;

import static java.lang.annotation.RetentionPolicy.SOURCE;


public interface EpisodeContract {
    interface View {
        void setFirstRow(String text);

        void setSecondRow(String text);

        void setThumbnail(String url);

        void setIcon(int drawableResourceId);

        void setTag(Object tag);

        void setPresenter(Presenter presenter);
    }

    interface Presenter {
        void update(Episode episode, View view);

        void itemClicked(Object tag, @Source int source);
    }

    @Retention(SOURCE)
    @IntDef({Source.ICON_IMAGE, Source.CONTAINER})
    @interface Source {
        int CONTAINER = 0;
        int ICON_IMAGE = 1;
    }
}
