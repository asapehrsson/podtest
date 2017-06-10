package podtest.jiki.se.podtest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Episodes {

    @SerializedName("episodes")
    @Expose
    private List<Episode> episodes = null;

    //@SerializedName("pagination")
    //@Expose
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
