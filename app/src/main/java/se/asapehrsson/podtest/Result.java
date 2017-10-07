package se.asapehrsson.podtest;

import se.asapehrsson.podtest.data.Episodes;

/**
 * Created by asapehrsson on 2017-07-08.
 */
public interface Result {
    void onSuccess(Episodes episode);

    void onFailed();
}
