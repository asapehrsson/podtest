package podtest.jiki.se.podtest;

import java.util.ArrayList;
import java.util.List;

import podtest.jiki.se.podtest.model.Episode;
import podtest.jiki.se.podtest.model.Episodes;
import podtest.jiki.se.podtest.model.Pagination;

public class LazyLoadedEpisodeList implements FetchEpisodesTask.Result {
    //Start and end dates as variables
    private static final String START_URL = "http://api.sr.se/api/v2/episodes/index?programid=2519&fromdate=2017-01-01&todate=2017-06-01&urltemplateid=3&audioquality=hi&format=json";

    private Pagination pagination;
    private List<Episode> episodes = new ArrayList<>();
    private ChangeListener<List<Episode>> changeListener;
    private boolean isLoading;

    public Episode getEpisode(int index) {
        Episode result = null;
        if (index < episodes.size()) {
            result = episodes.get(index);
        }
        return result;
    }

    //Trådpool
    public void loadEpisodes() {
        if (!isLoading) {
            isLoading = true;
            if (pagination == null) {
                new Thread(
                        new FetchEpisodesTask(this, START_URL)).start();
            } else if (pagination.getNextpage() != null) {
                new Thread(
                        new FetchEpisodesTask(this, pagination.getNextpage())).start();
            }
        }
    }

    public int getNoOfLoadedItems() {
        return episodes.size();
    }

    public int getTotalNoOfItems() {
        return pagination != null ? pagination.getTotalhits() : 0;
    }

    @Override
    public void onSuccess(Episodes value) {
        pagination = value.getPagination();
        episodes.addAll(value.getEpisodes());
        isLoading = false;
        if (changeListener != null) {
            changeListener.onChange(episodes);
        }
    }

    public boolean allItemsLoaded() {
        return (pagination != null) && pagination.getNextpage() == null;
    }

    @Override
    public void onFailed() {
        // Handle error!!
        isLoading = false;
    }

    public void setChangeListener(ChangeListener<List<Episode>> changeListener) {
        this.changeListener = changeListener;
    }
}
