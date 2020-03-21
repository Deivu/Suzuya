package suzuya.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.AudioManager;
import suzuya.client.SuzuyaClient;
import suzuya.structures.SuzuyaPlayerTrack;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

public class SuzuyaPlayer {
    public final SuzuyaClient suzuya;
    public final BlockingQueue<SuzuyaPlayerTrack> queue = new LinkedBlockingQueue<>();
    public final AudioPlayer player;
    public final VoiceChannel voiceChannel;
    public SuzuyaPlayerTrack currentTrack = null;

    private final Guild guild;
    private final AudioManager audioManager;
    private final TextChannel textChannel;

    int volume = 50;

    public SuzuyaPlayer(SuzuyaClient suzuya, TextChannel textChannel, VoiceChannel voiceChannel) {
        this.suzuya = suzuya;
        this.textChannel = textChannel;
        this.voiceChannel = voiceChannel;
        this.guild = textChannel.getGuild();

        player = suzuya.PlayerManager.createPlayer();
        player.addListener(new PlayerListener(this));
        audioManager = textChannel.getGuild().getAudioManager();
        audioManager.setSendingHandler(new PlayerSendHandler(player));
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
            audioManager.openAudioConnection(voiceChannel);
        }
        player.setFrameBufferDuration(500);
        suzuya.players.putIfAbsent(guild.getId(), this);
    }

    public void startPlaying(SuzuyaPlayerTrack suzuyaPlayerTrack) {
        currentTrack = suzuyaPlayerTrack;
        player.startTrack(suzuyaPlayerTrack.track, false);
    }

    public void setVolume(int volume) {
        this.volume = volume;
        player.setVolume(this.volume);
    }

    void handleMessage(MessageEmbed embed) {
        if (textChannel == null) return;
        textChannel.sendMessage(embed).queue();
    }

    CompletableFuture<Message> handleMessageFuture(MessageEmbed embed) {
        if (textChannel == null) return null;
        return textChannel.sendMessage(embed).submit();
    }

    void editMessage(String messageID, MessageEmbed embed) {
        if (textChannel == null) return;
        textChannel.editMessageById(messageID, embed).queue();
    }

    public void destroy() {
        if (queue.size() != 0) queue.clear();
        player.destroy();
        audioManager.closeAudioConnection();
        suzuya.players.remove(guild.getId());
    }

    String constructStatus(String status, String timeStart, String bar,  String timeEnd, String volumeIcon, String volumeStrength) {
        StringBuilder str = new StringBuilder();
        str.append("`")
                .append(status)
                .append("`")
                .append(" ")
                .append("`")
                .append(timeStart)
                .append(" ")
                .append(bar)
                .append(" ")
                .append(timeEnd)
                .append("`")
                .append(" ")
                .append("`")
                .append(volumeIcon)
                .append(volumeStrength)
                .append("`");
        return str.toString();
    }

    String playingStatus() {
        if (player.getPlayingTrack() != null && !player.isPaused()) return "▶️";
        if (player.isPaused()) return "⏸️";
        if (queue.size() == 0) return "⏹";
        return "⏭️";
    }

    String formatBar(Boolean ended) {
        AudioTrack current = currentTrack.track == null ? player.getPlayingTrack() : currentTrack.track;
        long pos = ended ? current.getDuration() : current.getPosition();
        double progress = (double) pos / current.getDuration();
        int limit = 14;
        int curr = (int)(progress * limit);
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < limit + 1; i++) {
            if (i == curr) str.append("\uD83D\uDD18");
            else str.append("⎯");
        }
        return str.toString();
    }

    String volumeIcon() {
        if(volume == 0) return "\uD83D\uDD07";
        if(volume < 30) return "\uD83D\uDD08";
        if(volume < 70) return "\uD83D\uDD09";
        return "\uD83D\uDD0A";
    }
}
