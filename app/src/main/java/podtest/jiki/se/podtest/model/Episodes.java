package podtest.jiki.se.podtest.model;

import java.util.List;

public class Episodes {

    private List<Episode> episodes = null;
    private Pagination pagination;

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public Pagination getPagination() {
        return pagination;
    }

    @Override
    public String toString() {
        return "No of episodes=" + episodes.size() + "(" + pagination.getTotalhits() + ")" + " page " + pagination.getPage() + "(" + pagination.getTotalpages() + ")";
    }
}
