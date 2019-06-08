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

public class PlayerListener extends AudioEventAdapter {
    private SuzuyaPlayer suzuyaPlayer;
    private SelfUser me;

    public PlayerListener(SuzuyaPlayer _suzuyaPlayer) {
        this.suzuyaPlayer = _suzuyaPlayer;
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
        MessageEmbed embed = new EmbedBuilder()
                .setColor(suzuyaPlayer.suzuya.defaultEmbedColor)
                .setTitle("Now Playing")
                .setDescription(track.getInfo().title)
                .setFooter(me.getName(), me.getAvatarUrl() != null ? me.getAvatarUrl() : me.getDefaultAvatarUrl())
                .setTimestamp(Instant.now())
                .build();
        suzuyaPlayer.itsSendMessageButWithHandling(suzuyaPlayer.textChannel.sendMessage(embed));
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            player.startTrack(suzuyaPlayer.queue.poll(), false);
        }

        // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
        //                       clone of this back to your queue
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        // An already playing track threw an exception (track end event will still be received separately)
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        player.startTrack(suzuyaPlayer.queue.poll(), false);
    }
}
