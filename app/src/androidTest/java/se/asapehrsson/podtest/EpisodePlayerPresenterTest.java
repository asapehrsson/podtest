package se.asapehrsson.podtest;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import se.asapehrsson.podtest.data.Episode;
import se.asapehrsson.podtest.miniplayer.PlayerPresenter;
import se.asapehrsson.podtest.miniplayer.PlayerContract;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class EpisodePlayerPresenterTest {
    private static final String TITLE = "Title";
    private static final String DESCRIPTION = "Description";
    @Mock Episode episode;
    @Mock PlayerContract.View view;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void happyTestUpdatePlayer() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();

        when(episode.getTitle()).thenReturn(TITLE);
        when(episode.getDescription()).thenReturn(DESCRIPTION);

        PlayerPresenter testSubject = new PlayerPresenter(context);
        testSubject.update(episode, view);

        verify(view).setFirstRow(eq(TITLE));
        verify(view).setSecondRow(eq(DESCRIPTION));
    }
}
