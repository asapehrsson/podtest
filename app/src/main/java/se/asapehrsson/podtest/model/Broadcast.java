package se.asapehrsson.podtest.model;

import java.util.List;

public class Broadcast {

    private String availablestoputc;
    private Playlist playlist;
    private List<Broadcastfile> broadcastfiles = null;

    public String getAvailablestoputc() {
        return availablestoputc;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public List<Broadcastfile> getBroadcastfiles() {
        return broadcastfiles;
    }

}
