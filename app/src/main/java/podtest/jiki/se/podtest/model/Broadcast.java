package podtest.jiki.se.podtest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Broadcast {

    @SerializedName("availablestoputc")
    @Expose
    private String availablestoputc;
    @SerializedName("playlist")
    @Expose
    private Playlist playlist;
    @SerializedName("broadcastfiles")
    @Expose
    private List<Broadcastfile> broadcastfiles = null;

    public String getAvailablestoputc() {
        return availablestoputc;
    }

    public void setAvailablestoputc(String availablestoputc) {
        this.availablestoputc = availablestoputc;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public List<Broadcastfile> getBroadcastfiles() {
        return broadcastfiles;
    }

    public void setBroadcastfiles(List<Broadcastfile> broadcastfiles) {
        this.broadcastfiles = broadcastfiles;
    }

}
