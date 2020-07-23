package suzuya.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.AudioManager;
import suzuya.client.SuzuyaClient;
import suzuya.structures.SuzuyaPlayerTrack;
import suzuya.util.TimeUtil;

import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SuzuyaPlayer {
    public final SuzuyaClient suzuya;
    public final BlockingQueue<SuzuyaPlayerTrack> queue = new LinkedBlockingQueue<>();
    public final AudioPlayer player;
    public final VoiceChannel voiceChannel;
    public SuzuyaPlayerTrack currentTrack = null;

    private final Guild guild;
    private final AudioManager audioManager;
    private final TextChannel textChannel;
    private final SelfUser me;

    final PlayerListener playerListener;
    int volume = 50;

    public SuzuyaPlayer(SuzuyaClient suzuya, TextChannel textChannel, VoiceChannel voiceChannel) throws Exception {
        this.audioManager = textChannel.getGuild().getAudioManager();
        if (audioManager.isConnected() || suzuya.players.containsKey(textChannel.getGuild().getId()))
            throw new Exception("Can't create a new Suzuya Player when there is still a working one");
        this.suzuya = suzuya;
        this.textChannel = textChannel;
        this.voiceChannel = voiceChannel;
        this.guild = textChannel.getGuild();
        this.me = suzuya.client.getSelfUser();
        this.player = suzuya.PlayerManager.createPlayer();
        this.playerListener = new PlayerListener(this);

        audioManager.openAudioConnection(voiceChannel);
        audioManager.setSendingHandler(new PlayerSendHandler(player));
        player.addListener(playerListener);
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

    public CompletableFuture<Message> sendPlayingMessage() {
        if (currentTrack == null) return null;
        String thumbnail = me.getAvatarUrl() != null ? me.getAvatarUrl() : me.getDefaultAvatarUrl();
        if (currentTrack.track.getSourceManager().getSourceName().equals("youtube")) thumbnail = "https://img.youtube.com/vi/" + currentTrack.track.getInfo().identifier + "/0.jpg";
        String startTime = TimeUtil.musicFormatTime(0);
        String endTime = TimeUtil.musicFormatTime(currentTrack.track.getDuration());
        playerListener.setPlayingEmbed(
                new EmbedBuilder()
                        .setColor(suzuya.defaultEmbedColor)
                        .setTitle("\\\uD83D\uDD17 Track Link", "https://www.youtube.com/watch?v=" + currentTrack.track.getInfo().identifier)
                        .setDescription(constructStatus(playingStatus(), startTime, formatBar(false), endTime, volumeIcon(), String.valueOf(volume)))
                        .addField("Now Playing", "`" + suzuya.util.trim(currentTrack.track.getInfo().title,  25) + "`", false)
                        .setThumbnail(thumbnail)
                        .setFooter("Uploader: " + currentTrack.track.getInfo().author , null)
                        .setTimestamp(Instant.now())
        );
        CompletableFuture<Message> msg = handleMessageFuture(playerListener.getPlayingEmbed().build());
        msg.thenAcceptAsync(message -> playerListener.setMessageID(message.getId()));
        msg.exceptionally(error -> {
            suzuya.util.errorTrace(error.getMessage(), error.getStackTrace());
            return null;
        });
        return msg;
    }

    void handleMessage(MessageEmbed embed) {
        if (textChannel == null) return;
        textChannel.sendMessage(embed).queue();
    }

    void editMessage(String messageID, MessageEmbed embed) {
        if (textChannel == null) return;
        textChannel.editMessageById(messageID, embed).queue();
    }

    CompletableFuture<Message> handleMessageFuture(MessageEmbed embed) {
        if (textChannel == null) return null;
        return textChannel.sendMessage(embed).submit();
    }

    public void destroy() {
        if (queue.size() != 0) queue.clear();
        player.destroy();
        audioManager.closeAudioConnection();
        suzuya.players.remove(guild.getId());
    }

    String constructStatus(String status, String timeStart, String bar,  String timeEnd, String volumeIcon, String volumeStrength) {
        return "`" + status + "`" + " " + "`" + timeStart + " " + bar + " " + timeEnd + "`" + " " + "`" + volumeIcon + volumeStrength + "`";
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
