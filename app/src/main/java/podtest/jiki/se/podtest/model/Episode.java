package podtest.jiki.se.podtest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Episode {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("program")
    @Expose
    private Program program;
    @SerializedName("audiopreference")
    @Expose
    private String audiopreference;
    @SerializedName("publishdateutc")
    @Expose
    private String publishdateutc;
    @SerializedName("imageurl")
    @Expose
    private String imageurl;
    @SerializedName("imageurltemplate")
    @Expose
    private String imageurltemplate;
    @SerializedName("broadcast")
    @Expose
    private Broadcast broadcast;
    @SerializedName("listenpodfile")
    @Expose
    private Listenpodfile listenpodfile;
    @SerializedName("downloadpodfile")
    @Expose
    private Downloadpodfile downloadpodfile;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public String getAudiopreference() {
        return audiopreference;
    }

    public void setAudiopreference(String audiopreference) {
        this.audiopreference = audiopreference;
    }

    public String getPublishdateutc() {
        return publishdateutc;
    }

    public void setPublishdateutc(String publishdateutc) {
        this.publishdateutc = publishdateutc;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getImageurltemplate() {
        return imageurltemplate;
    }

    public void setImageurltemplate(String imageurltemplate) {
        this.imageurltemplate = imageurltemplate;
    }

    public Broadcast getBroadcast() {
        return broadcast;
    }

    public void setBroadcast(Broadcast broadcast) {
        this.broadcast = broadcast;
    }

    public Listenpodfile getListenpodfile() {
        return listenpodfile;
    }

    public void setListenpodfile(Listenpodfile listenpodfile) {
        this.listenpodfile = listenpodfile;
    }

    public Downloadpodfile getDownloadpodfile() {
        return downloadpodfile;
    }

    public void setDownloadpodfile(Downloadpodfile downloadpodfile) {
        this.downloadpodfile = downloadpodfile;
    }

}
