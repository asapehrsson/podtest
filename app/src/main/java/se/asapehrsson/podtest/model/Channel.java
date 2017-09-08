package se.asapehrsson.podtest.model;

public class Channel {

    private String image;
    private String imagetemplate;
    private String color;
    private String siteurl;
    private Liveaudio liveaudio;
    private String scheduleurl;
    private String channeltype;
    private String xmltvid;
    private Integer id;
    private String name;

    public String getImage() {
        return image;
    }

    public String getImagetemplate() {
        return imagetemplate;
    }

    public String getColor() {
        return color;
    }

    public String getSiteurl() {
        return siteurl;
    }

    public Liveaudio getLiveaudio() {
        return liveaudio;
    }

    public String getScheduleurl() {
        return scheduleurl;
    }

    public String getChanneltype() {
        return channeltype;
    }

    public String getXmltvid() {
        return xmltvid;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}