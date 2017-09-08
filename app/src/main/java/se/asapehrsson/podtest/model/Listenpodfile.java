package se.asapehrsson.podtest.model;

public class Listenpodfile {

    private String title;
    private String description;
    private Integer filesizeinbytes;
    private Program program;
    private Integer duration;
    private String publishdateutc;
    private Integer id;
    private String url;
    private String statkey;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getFilesizeinbytes() {
        return filesizeinbytes;
    }

    public Program getProgram() {
        return program;
    }

    public Integer getDuration() {
        return duration;
    }

    public String getPublishdateutc() {
        return publishdateutc;
    }

    public Integer getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getStatkey() {
        return statkey;
    }
}
