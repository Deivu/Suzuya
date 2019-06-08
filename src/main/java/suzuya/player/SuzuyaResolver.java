package suzuya.player;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import suzuya.structures.SuzuyaTrack;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class SuzuyaResolver implements AudioLoadResultHandler {
    private final AudioPlayerManager playerManager;
    private final CompletableFuture<SuzuyaTrack> result = new CompletableFuture<>();

    public SuzuyaResolver(AudioPlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public CompletableFuture<SuzuyaTrack> resolve(String link) {
        this.playerManager.loadItem(link, this);
        return result;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        ArrayList<AudioTrack> tracks = new ArrayList<>();
        tracks.add(track);
        this.result.complete(new SuzuyaTrack(tracks, null, "LOADED"));
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        String p_name = null;
        String result = null;
        if (playlist.isSearchResult()) {
            result = "SEARCH";
        } else {
            p_name = playlist.getName();
            result = "PLAYLIST";
        }
        this.result.complete(new SuzuyaTrack(new ArrayList<>(playlist.getTracks()), p_name, result));
    }

    @Override
    public void noMatches() {
        this.result.complete(new SuzuyaTrack(new ArrayList<>(), null, "NO_MATCHES"));
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        exception.printStackTrace();
        this.result.complete(new SuzuyaTrack(new ArrayList<>(), null, "FAILED"));
    }
}
