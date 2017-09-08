package se.asapehrsson.podtest.model;

public class Episode {

    private Integer id;
    private String title;
    private String description;
    private String url;
    private Program program;
    private String audiopreference;
    private String publishdateutc;
    private String imageurl;
    private String imageurltemplate;
    private Broadcast broadcast;
    private Listenpodfile listenpodfile;
    private Downloadpodfile downloadpodfile;

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public Program getProgram() {
        return program;
    }


    public String getAudiopreference() {
        return audiopreference;
    }


    public String getPublishdateutc() {
        return publishdateutc;
    }


    public String getImageurl() {
        return imageurl;
    }


    public String getImageurltemplate() {
        return imageurltemplate;
    }


    public Broadcast getBroadcast() {
        return broadcast;
    }


    public Listenpodfile getListenpodfile() {
        return listenpodfile;
    }


    public Downloadpodfile getDownloadpodfile() {
        return downloadpodfile;
    }


}
