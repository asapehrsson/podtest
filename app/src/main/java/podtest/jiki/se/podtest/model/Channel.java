package podtest.jiki.se.podtest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Channel {

    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("imagetemplate")
    @Expose
    private String imagetemplate;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("siteurl")
    @Expose
    private String siteurl;
    @SerializedName("liveaudio")
    @Expose
    private Liveaudio liveaudio;
    @SerializedName("scheduleurl")
    @Expose
    private String scheduleurl;
    @SerializedName("channeltype")
    @Expose
    private String channeltype;
    @SerializedName("xmltvid")
    @Expose
    private String xmltvid;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImagetemplate() {
        return imagetemplate;
    }

    public void setImagetemplate(String imagetemplate) {
        this.imagetemplate = imagetemplate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSiteurl() {
        return siteurl;
    }

    public void setSiteurl(String siteurl) {
        this.siteurl = siteurl;
    }

    public Liveaudio getLiveaudio() {
        return liveaudio;
    }

    public void setLiveaudio(Liveaudio liveaudio) {
        this.liveaudio = liveaudio;
    }

    public String getScheduleurl() {
        return scheduleurl;
    }

    public void setScheduleurl(String scheduleurl) {
        this.scheduleurl = scheduleurl;
    }

    public String getChanneltype() {
        return channeltype;
    }

    public void setChanneltype(String channeltype) {
        this.channeltype = channeltype;
    }

    public String getXmltvid() {
        return xmltvid;
    }

    public void setXmltvid(String xmltvid) {
        this.xmltvid = xmltvid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}