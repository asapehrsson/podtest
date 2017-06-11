package podtest.jiki.se.podtest.start;


import podtest.jiki.se.podtest.model.Episode;

public interface EpisodeViewer {
    void showInfo(Episode episode);

    void play(Episode episode);
}
