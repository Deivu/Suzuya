package suzuya.structures;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;

public class SuzuyaTrack {
    public ArrayList<AudioTrack> tracks;
    public String playlist;
    public String result;

    public SuzuyaTrack(ArrayList<AudioTrack> tracks, String playlist, String result) {
        this.tracks = tracks;
        this.playlist = playlist;
        this.result = result;
    }
}
