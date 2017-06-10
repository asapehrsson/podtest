package podtest.jiki.se.podtest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Socialmediaplatform {

    @SerializedName("platform")
    @Expose
    private String platform;
    @SerializedName("platformurl")
    @Expose
    private String platformurl;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPlatformurl() {
        return platformurl;
    }

    public void setPlatformurl(String platformurl) {
        this.platformurl = platformurl;
    }

}