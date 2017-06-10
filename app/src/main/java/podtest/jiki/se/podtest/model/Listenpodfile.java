package podtest.jiki.se.podtest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Listenpodfile {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("filesizeinbytes")
    @Expose
    private Integer filesizeinbytes;
    @SerializedName("program")
    @Expose
    private Program program;
    @SerializedName("duration")
    @Expose
    private Integer duration;
    @SerializedName("publishdateutc")
    @Expose
    private String publishdateutc;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("statkey")
    @Expose
    private String statkey;

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

    public Integer getFilesizeinbytes() {
        return filesizeinbytes;
    }

    public void setFilesizeinbytes(Integer filesizeinbytes) {
        this.filesizeinbytes = filesizeinbytes;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getPublishdateutc() {
        return publishdateutc;
    }

    public void setPublishdateutc(String publishdateutc) {
        this.publishdateutc = publishdateutc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatkey() {
        return statkey;
    }

    public void setStatkey(String statkey) {
        this.statkey = statkey;
    }

}
