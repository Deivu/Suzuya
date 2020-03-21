package suzuya.player;

import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;
import suzuya.util.TimeUtil;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

class PlayerListener extends AudioEventAdapter {
    private final SuzuyaPlayer suzuyaPlayer;
    private final SelfUser me;

    private String messageID = null;
    private ScheduledFuture<?> editCron = null;
    private EmbedBuilder playingEmbed = null;

    PlayerListener(SuzuyaPlayer suzuyaPlayer) {
        this.suzuyaPlayer = suzuyaPlayer;
        this.me = suzuyaPlayer.suzuya.client.getSelfUser();
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {}

    @Override
    public void onPlayerResume(AudioPlayer player) {}

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        if (suzuyaPlayer.volume != player.getVolume()) player.setVolume(suzuyaPlayer.volume);
        String thumbnail = me.getAvatarUrl() != null ? me.getAvatarUrl() : me.getDefaultAvatarUrl();
        if (track.getSourceManager().getSourceName().equals("youtube")) thumbnail = "https://img.youtube.com/vi/" + track.getInfo().identifier + "/0.jpg";

        String startTime = TimeUtil.musicFormatTime(0);
        String endTime = TimeUtil.musicFormatTime(track.getDuration());

        playingEmbed = new EmbedBuilder()
                .setColor(suzuyaPlayer.suzuya.defaultEmbedColor)
                .setTitle("\\\uD83D\uDD17 Track Link", "https://www.youtube.com/watch?v=" + track.getInfo().identifier)
                .setDescription(this.suzuyaPlayer.constructStatus(suzuyaPlayer.playingStatus(), startTime, suzuyaPlayer.formatBar(false), endTime, suzuyaPlayer.volumeIcon(), String.valueOf(suzuyaPlayer.volume)))
                .addField("Now Playing", "`" + this.suzuyaPlayer.suzuya.util.trim(track.getInfo().title,  25) + "`", false)
                .setThumbnail(thumbnail)
                .setFooter("Uploader: " + track.getInfo().author , null)
                .setTimestamp(Instant.now());
        CompletableFuture<Message> sent = suzuyaPlayer.handleMessageFuture(playingEmbed.build());
        sent.thenAcceptAsync(message -> {
                    messageID = message.getId();
                    editCron = this.suzuyaPlayer.suzuya.scheduler.scheduleAtFixedRate(this::onTrackUpdate, 5, 5, TimeUnit.SECONDS);
                });
        sent.exceptionally(error -> {
            suzuyaPlayer.suzuya.util.errorTrace(error.getMessage(), error.getStackTrace());
            return null;
        });
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        this.onEndEmbed();
        suzuyaPlayer.currentTrack = null;
        if (suzuyaPlayer.queue.size() == 0) {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(suzuyaPlayer.suzuya.defaultEmbedColor)
                    .setTitle("\\⏏ Queue is empty")
                    .setDescription("Player cleaned up. You are free to start a new one again.")
                    .build();
            suzuyaPlayer.handleMessage(embed);
            suzuyaPlayer.destroy();
            return;
        }
        suzuyaPlayer.startPlaying(suzuyaPlayer.queue.poll());
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        String friendlyError;
        if (exception.severity.equals(FriendlyException.Severity.COMMON)) {
            friendlyError = exception.getMessage();
            suzuyaPlayer.suzuya.util.errorTrace(friendlyError);
        } else {
            suzuyaPlayer.suzuya.util.errorTrace(exception.getMessage(), exception.getStackTrace());
            friendlyError = exception.getMessage();
        }
        MessageEmbed embed = new EmbedBuilder()
                .setColor(suzuyaPlayer.suzuya.defaultEmbedColor)
                .setTitle("\\❌ Player Error, Skipping the track.")
                .setDescription("```" + friendlyError + "```")
                .setTimestamp(Instant.now())
                .build();
        suzuyaPlayer.handleMessage(embed);
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        this.onTrackEnd(player, track, AudioTrackEndReason.FINISHED);
    }
    
    private void onTrackUpdate() {
        if (playingEmbed == null || messageID == null || suzuyaPlayer.currentTrack == null) {
            editCron.cancel(true);
            messageID = null;
            return;
        }
        if (suzuyaPlayer.player.isPaused()) return;
        AudioTrack current = suzuyaPlayer.currentTrack.track;
        String startTime = TimeUtil.musicFormatTime(current.getPosition());
        String endTime = TimeUtil.musicFormatTime(current.getDuration());
        playingEmbed.setDescription(this.suzuyaPlayer.constructStatus(suzuyaPlayer.playingStatus(), startTime, suzuyaPlayer.formatBar(false), endTime, suzuyaPlayer.volumeIcon(), String.valueOf(suzuyaPlayer.volume)));
        suzuyaPlayer.editMessage(messageID, playingEmbed.build());
    }

    private void onEndEmbed() {
        if (editCron == null) return;
        editCron.cancel(true);
        if (playingEmbed == null || messageID == null || suzuyaPlayer.currentTrack == null) return;
        AudioTrack current = suzuyaPlayer.currentTrack.track;
        String endTime = TimeUtil.musicFormatTime(current.getDuration());
        playingEmbed.setDescription(this.suzuyaPlayer.constructStatus(suzuyaPlayer.playingStatus(), endTime, suzuyaPlayer.formatBar(true), endTime, suzuyaPlayer.volumeIcon(), String.valueOf(suzuyaPlayer.volume)));
        suzuyaPlayer.editMessage(messageID, playingEmbed.build());
        messageID = null;
        playingEmbed = null;
    }
}
