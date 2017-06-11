package podtest.jiki.se.podtest.model;

public class Pagination {

    private Integer page;
    private Integer size;
    private Integer totalhits;
    private Integer totalpages;
    private String nextpage;

    public Integer getPage() {
        return page;
    }

    public Integer getSize() {
        return size;
    }

    public Integer getTotalhits() {
        return totalhits;
    }

    public Integer getTotalpages() {
        return totalpages;
    }


    public String getNextpage() {
        return nextpage;
    }


}
