package se.asapehrsson.podtest;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import se.asapehrsson.podtest.data.Episode;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EpisodeListViewPresenterUnitTest {
    private static final String TITLE = "Title";
    private static final String DESCRIPTION = "Description";
    @Mock
    Episode episode;
    @Mock
    EpisodeContract.View view;
    @Mock
    EpisodeViewer episodeViewer;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void happyTestUpdateRow() throws Exception {
        when(episode.getTitle()).thenReturn(TITLE);
        when(episode.getDescription()).thenReturn(DESCRIPTION);

        EpisodeListViewPresenter testSubject = new EpisodeListViewPresenter(episodeViewer);
        testSubject.update(episode, view);

        verify(view).setFirstRow(eq(TITLE));
        verify(view).setSecondRow(eq(DESCRIPTION));
    }

}