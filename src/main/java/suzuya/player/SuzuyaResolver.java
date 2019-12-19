package suzuya.player;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import suzuya.client.SuzuyaClient;
import suzuya.structures.SuzuyaResult;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class SuzuyaResolver implements AudioLoadResultHandler {
    private final AudioPlayerManager playerManager;
    private final SuzuyaClient suzuya;
    private final CompletableFuture<SuzuyaResult> result = new CompletableFuture<>();

    public SuzuyaResolver(SuzuyaClient suzuya) {
        this.suzuya = suzuya;
        this.playerManager = suzuya.PlayerManager;
    }

    public CompletableFuture<SuzuyaResult> resolve(String link) {
        this.playerManager.loadItem(link, this);
        return result;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        ArrayList<AudioTrack> tracks = new ArrayList<>();
        tracks.add(track);
        this.result.complete(new SuzuyaResult(tracks, null, "LOADED"));
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        this.result.complete(new SuzuyaResult(new ArrayList<>(playlist.getTracks()), playlist.getName(), playlist.isSearchResult() ? "SEARCH" : "PLAYLIST"));
    }

    @Override
    public void noMatches() {
        this.result.complete(new SuzuyaResult(new ArrayList<>(), null, "NO_MATCHES"));
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        if (!exception.severity.equals(FriendlyException.Severity.COMMON)) {
            suzuya.errorTrace(exception.getMessage(), exception.getStackTrace());
            this.result.complete(new SuzuyaResult(new ArrayList<>(), null, "FAILED"));
        } else {
            this.noMatches();
        }
    }
}
