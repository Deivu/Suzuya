package suzuya.player;

import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.SelfUser;
import suzuya.util.TimeUtil;

import java.time.Instant;

class PlayerListener extends AudioEventAdapter {
    private final SuzuyaPlayer suzuyaPlayer;
    private final SelfUser me;

    public PlayerListener(SuzuyaPlayer suzuyaPlayer) {
        this.suzuyaPlayer = suzuyaPlayer;
        this.me = suzuyaPlayer.suzuya.client.getSelfUser();
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        // Player was paused
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        // Player was resumed
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        suzuyaPlayer.currentTrack = track;
        if (suzuyaPlayer.volume != player.getVolume()) player.setVolume(suzuyaPlayer.volume);
        String thumbnail = me.getAvatarUrl() != null ? me.getAvatarUrl() : me.getDefaultAvatarUrl();
        if (track.getSourceManager().getSourceName().equals("youtube")) thumbnail = "https://img.youtube.com/vi/" + track.getInfo().identifier + "/0.jpg";
        MessageEmbed embed = new EmbedBuilder()
                .setColor(suzuyaPlayer.suzuya.defaultEmbedColor)
                .setTitle("\\▶ Now Playing")
                .setDescription("`" + track.getInfo().title + "`")
                .setThumbnail(thumbnail)
                .setFooter("\uD83C\uDFB5 | " + TimeUtil.getDurationBreakdown(track.getDuration(), false), me.getAvatarUrl() != null ? me.getAvatarUrl() : me.getDefaultAvatarUrl())
                .setTimestamp(Instant.now())
                .build();
        suzuyaPlayer.handleMessage(embed);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        suzuyaPlayer.currentTrack = null;
        if (endReason.name().equals("REPLACED")) return;
        if (suzuyaPlayer.queue.size() == 0) {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(suzuyaPlayer.suzuya.defaultEmbedColor)
                    .setTitle("\\\u23F9 Queue is empty")
                    .setDescription("Player cleaned up. You are free to start a new one again.")
                    .setFooter(me.getName(), me.getAvatarUrl() != null ? me.getAvatarUrl() : me.getDefaultAvatarUrl())
                    .build();
            suzuyaPlayer.handleMessage(embed);
            suzuyaPlayer.destroy();
            return;
        }
        player.startTrack(suzuyaPlayer.queue.poll(), false);
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        suzuyaPlayer.currentTrack = null;
        String friendlyError;
        if (exception.severity.equals(FriendlyException.Severity.COMMON)) {
            friendlyError = exception.getMessage();
            suzuyaPlayer.suzuya.errorTrace(friendlyError);
        } else {
            suzuyaPlayer.suzuya.errorTrace(exception.getMessage(), exception.getStackTrace());
            friendlyError = "Something " + exception.severity.name() +" bizarre happened.";
        }
        MessageEmbed embed = new EmbedBuilder()
                .setColor(suzuyaPlayer.suzuya.defaultEmbedColor)
                .setTitle("\\❌ Player Error, Skipping the track.")
                .setDescription("```" + friendlyError + "```")
                .setFooter(me.getName(), me.getAvatarUrl() != null ? me.getAvatarUrl() : me.getDefaultAvatarUrl())
                .setTimestamp(Instant.now())
                .build();
        suzuyaPlayer.handleMessage(embed);
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        suzuyaPlayer.currentTrack = null;
        suzuyaPlayer.handleMessage("Player is stuck, skipping the track..");
        if (suzuyaPlayer.queue.size() == 0) {
            suzuyaPlayer.queue.clear();
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(suzuyaPlayer.suzuya.defaultEmbedColor)
                    .setTitle("\\\u23F9 Queue is empty")
                    .setDescription("Player cleaned up. You are free to start a new one again.")
                    .setFooter(me.getName(), me.getAvatarUrl() != null ? me.getAvatarUrl() : me.getDefaultAvatarUrl())
                    .setTimestamp(Instant.now())
                    .build();
            suzuyaPlayer.handleMessage(embed);
            suzuyaPlayer.destroy();
            return;
        }
        player.startTrack(suzuyaPlayer.queue.poll(), false);
    }
}
