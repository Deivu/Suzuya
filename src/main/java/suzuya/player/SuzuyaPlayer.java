package suzuya.player;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import net.dv8tion.jda.core.requests.restaction.MessageAction;
import suzuya.SuzuyaClient;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SuzuyaPlayer {
    public final SuzuyaClient suzuya;
    public final BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();

    public AudioPlayer player;
    public AudioManager audioManager;
    public TextChannel textChannel;
    public Guild guild;
    public VoiceChannel voiceChannel;

    public SuzuyaPlayer(SuzuyaClient _suzuya, TextChannel _textChannel, VoiceChannel _voiceChannel) {
        this.suzuya = _suzuya;
        this.textChannel = _textChannel;
        this.voiceChannel = _voiceChannel;
        this.guild = _textChannel.getGuild();
        init();
    }

    private void init() {
        if (suzuya.players.containsKey(guild.getId())) return;
        player = suzuya.PlayerManager.createPlayer();
        player.addListener(new PlayerListener(this));
        audioManager = textChannel.getGuild().getAudioManager();
        audioManager.setSendingHandler(new PlayerSendHandler(player));
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
            audioManager.openAudioConnection(voiceChannel);
        }
        suzuya.players.put(guild.getId(), this);
    }

    public void actuallyResolveThing(String someCrap) {
        suzuya.PlayerManager.loadItem(someCrap, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                queue.offer(track);
                itsSendMessageButWithHandling(textChannel.sendMessage("Added the track " + track.getInfo().title + " in the queue"));
                player.startTrack(track, false);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track: playlist.getTracks()) {
                    queue.offer(track);
                }
                itsSendMessageButWithHandling(textChannel.sendMessage("Added the playlist " + playlist.getName() + " in the queue"));
                player.startTrack(queue.poll(), false);
            }

            @Override
            public void noMatches() {
                itsSendMessageButWithHandling(textChannel.sendMessage("No results found...."));
                if (audioManager.isConnected()) {
                    audioManager.closeAudioConnection();
                }
                suzuya.players.remove(guild.getId());
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                if (exception.severity.name().equals("COMMON")) {
                    itsSendMessageButWithHandling(textChannel.sendMessage("Admiral, an error occurred when trying to add this track/playlist. Reason: `" + exception.getMessage() + "`"));
                } else {
                    itsSendMessageButWithHandling(textChannel.sendMessage("Admiral, an error occurred when trying to add this track/playlist."));
                }
                exception.printStackTrace();
                if (audioManager.isConnected()) {
                    audioManager.closeAudioConnection();
                }
                suzuya.players.remove(guild.getId());
            }
        });
    }

    public void itsSendMessageButWithHandling(MessageAction action) {
        try {
            action.queue();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }
}
