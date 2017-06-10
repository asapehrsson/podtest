package podtest.jiki.se.podtest.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Program {

    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("programcategory")
    @Expose
    private Programcategory programcategory;
    @SerializedName("broadcastinfo")
    @Expose
    private String broadcastinfo;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("programurl")
    @Expose
    private String programurl;
    @SerializedName("programimage")
    @Expose
    private String programimage;
    @SerializedName("programimagetemplate")
    @Expose
    private String programimagetemplate;
    @SerializedName("programimagewide")
    @Expose
    private String programimagewide;
    @SerializedName("programimagetemplatewide")
    @Expose
    private String programimagetemplatewide;
    @SerializedName("socialimage")
    @Expose
    private String socialimage;
    @SerializedName("socialimagetemplate")
    @Expose
    private String socialimagetemplate;
    @SerializedName("socialmediaplatforms")
    @Expose
    private List<Socialmediaplatform> socialmediaplatforms = null;
    @SerializedName("channel")
    @Expose
    private Channel channel;
    @SerializedName("archived")
    @Expose
    private Boolean archived;
    @SerializedName("hasondemand")
    @Expose
    private Boolean hasondemand;
    @SerializedName("haspod")
    @Expose
    private Boolean haspod;
    @SerializedName("responsibleeditor")
    @Expose
    private String responsibleeditor;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("payoff")
    @Expose
    private String payoff;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Programcategory getProgramcategory() {
        return programcategory;
    }

    public void setProgramcategory(Programcategory programcategory) {
        this.programcategory = programcategory;
    }

    public String getBroadcastinfo() {
        return broadcastinfo;
    }

    public void setBroadcastinfo(String broadcastinfo) {
        this.broadcastinfo = broadcastinfo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProgramurl() {
        return programurl;
    }

    public void setProgramurl(String programurl) {
        this.programurl = programurl;
    }

    public String getProgramimage() {
        return programimage;
    }

    public void setProgramimage(String programimage) {
        this.programimage = programimage;
    }

    public String getProgramimagetemplate() {
        return programimagetemplate;
    }

    public void setProgramimagetemplate(String programimagetemplate) {
        this.programimagetemplate = programimagetemplate;
    }

    public String getProgramimagewide() {
        return programimagewide;
    }

    public void setProgramimagewide(String programimagewide) {
        this.programimagewide = programimagewide;
    }

    public String getProgramimagetemplatewide() {
        return programimagetemplatewide;
    }

    public void setProgramimagetemplatewide(String programimagetemplatewide) {
        this.programimagetemplatewide = programimagetemplatewide;
    }

    public String getSocialimage() {
        return socialimage;
    }

    public void setSocialimage(String socialimage) {
        this.socialimage = socialimage;
    }

    public String getSocialimagetemplate() {
        return socialimagetemplate;
    }

    public void setSocialimagetemplate(String socialimagetemplate) {
        this.socialimagetemplate = socialimagetemplate;
    }

    public List<Socialmediaplatform> getSocialmediaplatforms() {
        return socialmediaplatforms;
    }

    public void setSocialmediaplatforms(List<Socialmediaplatform> socialmediaplatforms) {
        this.socialmediaplatforms = socialmediaplatforms;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Boolean getHasondemand() {
        return hasondemand;
    }

    public void setHasondemand(Boolean hasondemand) {
        this.hasondemand = hasondemand;
    }

    public Boolean getHaspod() {
        return haspod;
    }

    public void setHaspod(Boolean haspod) {
        this.haspod = haspod;
    }

    public String getResponsibleeditor() {
        return responsibleeditor;
    }

    public void setResponsibleeditor(String responsibleeditor) {
        this.responsibleeditor = responsibleeditor;
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

    public String getPayoff() {
        return payoff;
    }

    public void setPayoff(String payoff) {
        this.payoff = payoff;
    }

}
