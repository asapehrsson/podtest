package se.asapehrsson.podtest.model;


import java.util.List;

public class Program {

    private String description;
    private Programcategory programcategory;
    private String broadcastinfo;
    private String email;
    private String phone;
    private String programurl;
    private String programimage;
    private String programimagetemplate;
    private String programimagewide;
    private String programimagetemplatewide;
    private String socialimage;
    private String socialimagetemplate;
    private List<Socialmediaplatform> socialmediaplatforms = null;
    private Channel channel;
    private Boolean archived;
    private Boolean hasondemand;
    private Boolean haspod;
    private String responsibleeditor;
    private Integer id;
    private String name;
    private String payoff;

    public String getDescription() {
        return description;
    }

    public Programcategory getProgramcategory() {
        return programcategory;
    }

    public String getBroadcastinfo() {
        return broadcastinfo;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getProgramurl() {
        return programurl;
    }

    public String getProgramimage() {
        return programimage;
    }

    public String getProgramimagetemplate() {
        return programimagetemplate;
    }

    public String getProgramimagewide() {
        return programimagewide;
    }

    public String getProgramimagetemplatewide() {
        return programimagetemplatewide;
    }

    public String getSocialimage() {
        return socialimage;
    }

    public String getSocialimagetemplate() {
        return socialimagetemplate;
    }

    public List<Socialmediaplatform> getSocialmediaplatforms() {
        return socialmediaplatforms;
    }

    public Channel getChannel() {
        return channel;
    }

    public Boolean getArchived() {
        return archived;
    }

    public Boolean getHasondemand() {
        return hasondemand;
    }

    public Boolean getHaspod() {
        return haspod;
    }

    public String getResponsibleeditor() {
        return responsibleeditor;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPayoff() {
        return payoff;
    }
}
