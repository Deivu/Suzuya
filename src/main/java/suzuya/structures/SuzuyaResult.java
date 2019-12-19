package suzuya.structures;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;

public class SuzuyaResult {
    public final ArrayList<AudioTrack> tracks;
    public final String playlist;
    public final String result;

    public SuzuyaResult(ArrayList<AudioTrack> tracks, String playlist, String result) {
        this.tracks = tracks;
        this.playlist = playlist;
        this.result = result;
    }
}
