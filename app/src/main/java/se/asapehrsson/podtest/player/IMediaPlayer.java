package se.asapehrsson.podtest.player;

/**
 * Created by aasapehrsson on 2017-09-27.
 */

public interface IMediaPlayer {
    void loadAndPlay(String url);

    void play();

    void pause();

    void release();

    boolean isPlaying();
}
