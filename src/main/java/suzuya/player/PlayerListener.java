package suzuya.player;

import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.SelfUser;

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
        if (suzuyaPlayer.volume != player.getVolume()) player.setVolume(suzuyaPlayer.volume);
        MessageEmbed embed = new EmbedBuilder()
                .setColor(suzuyaPlayer.suzuya.defaultEmbedColor)
                .setTitle("Now Playing")
                .setDescription(track.getInfo().title)
                .setFooter(me.getName(), me.getAvatarUrl() != null ? me.getAvatarUrl() : me.getDefaultAvatarUrl())
                .setTimestamp(Instant.now())
                .build();
        suzuyaPlayer.handleMessage(embed);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.name().equals("REPLACED")) return;
        AudioTrack toPlay = suzuyaPlayer.queue.poll();
        if (suzuyaPlayer.queue.size() == 0) {
            suzuyaPlayer.queue.clear();
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(suzuyaPlayer.suzuya.defaultEmbedColor)
                    .setTitle("Player Ended")
                    .setDescription("You are free to start a new one again.")
                    .setFooter(me.getName(), me.getAvatarUrl() != null ? me.getAvatarUrl() : me.getDefaultAvatarUrl())
                    .setTimestamp(Instant.now())
                    .build();
            suzuyaPlayer.handleMessage(embed);
            suzuyaPlayer.destroy();
            return;
        }
        player.startTrack(toPlay, false);
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
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
                .setTitle("Player Error, Skipping the track.")
                .setDescription("```" + friendlyError + "```")
                .setFooter(me.getName(), me.getAvatarUrl() != null ? me.getAvatarUrl() : me.getDefaultAvatarUrl())
                .setTimestamp(Instant.now())
                .build();
        suzuyaPlayer.handleMessage(embed);
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        suzuyaPlayer.handleMessage("Player is stuck, skipping the track..");
        player.startTrack(suzuyaPlayer.queue.poll(), false);
    }
}
