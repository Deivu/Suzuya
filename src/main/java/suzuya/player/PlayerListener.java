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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

class PlayerListener extends AudioEventAdapter {
    private final SuzuyaPlayer suzuyaPlayer;
    private final SelfUser me;

    private String messageID = null;
    private ScheduledFuture<?> editCron = null;
    private EmbedBuilder playingEmbed = null;

    public PlayerListener(SuzuyaPlayer suzuyaPlayer) {
        this.suzuyaPlayer = suzuyaPlayer;
        this.me = suzuyaPlayer.suzuya.client.getSelfUser();
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {}

    @Override
    public void onPlayerResume(AudioPlayer player) {}

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        suzuyaPlayer.currentTrack = track;
        if (suzuyaPlayer.volume != player.getVolume()) player.setVolume(suzuyaPlayer.volume);
        String thumbnail = me.getAvatarUrl() != null ? me.getAvatarUrl() : me.getDefaultAvatarUrl();
        if (track.getSourceManager().getSourceName().equals("youtube")) thumbnail = "https://img.youtube.com/vi/" + track.getInfo().identifier + "/0.jpg";
        String startTime = TimeUtil.musicFormatTime(0);
        String endTime = TimeUtil.musicFormatTime(track.getDuration());
        playingEmbed = new EmbedBuilder()
                .setColor(suzuyaPlayer.suzuya.defaultEmbedColor)
                .setTitle("\\\uD83D\uDD17 Track Link", "https://www.youtube.com/watch?v=" + track.getInfo().identifier)
                .setDescription("\\▶️ `" + startTime + "/" + endTime + "` | \\\uD83D\uDD0A `" + suzuyaPlayer.volume + "%`")
                .addField(
                        "Now Playing",
                        "`" + track.getInfo().title + "`",
                        false
                )
                .setThumbnail(thumbnail)
                .setFooter("Uploader: " + track.getInfo().author , null)
                .setTimestamp(Instant.now());
        suzuyaPlayer.handleMessageFuture(playingEmbed.build())
                .exceptionally(error -> {
                    suzuyaPlayer.suzuya.errorTrace(error.getMessage(), error.getStackTrace());
                    return null;
                })
                .thenAcceptAsync(message -> {
                    messageID = message.getId();
                    editCron = this.suzuyaPlayer.suzuya.scheduler.scheduleAtFixedRate(this::onTrackUpdate, 5, 5, TimeUnit.SECONDS);
                });
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        this.onEndEmbed();
        suzuyaPlayer.currentTrack = null;
        if (endReason.name().equals("REPLACED")) return;
        if (suzuyaPlayer.queue.size() == 0) {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(suzuyaPlayer.suzuya.defaultEmbedColor)
                    .setTitle("\\⏏ Queue is empty")
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
        this.onEndEmbed();
        suzuyaPlayer.currentTrack = null;
        if (suzuyaPlayer.queue.size() == 0) {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(suzuyaPlayer.suzuya.defaultEmbedColor)
                    .setTitle("\\⏏ Queue is empty")
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
    
    private void onTrackUpdate() {
        if (playingEmbed == null || messageID == null || suzuyaPlayer.currentTrack == null) {
            editCron.cancel(true);
            messageID = null;
            return;
        }
        if (suzuyaPlayer.player.isPaused()) return;
        String startTime = TimeUtil.musicFormatTime(suzuyaPlayer.currentTrack.getPosition());
        String endTime = TimeUtil.musicFormatTime(suzuyaPlayer.currentTrack.getDuration());
        playingEmbed.setDescription("\\▶️ `" + startTime + "/" + endTime + "` | \\\uD83D\uDD0A `" + suzuyaPlayer.volume + "%`");
        suzuyaPlayer.editMessage(messageID, playingEmbed.build());
    }

    private void onEndEmbed() {
        if (editCron == null) return;
        editCron.cancel(true);
        if (playingEmbed == null || messageID == null || suzuyaPlayer.currentTrack == null) return;
        String endTime = TimeUtil.musicFormatTime(suzuyaPlayer.currentTrack.getDuration());
        if (suzuyaPlayer.queue.size() == 0) {
            playingEmbed.setDescription("\\⏹  `" + endTime + "/" + endTime + "` | \\\uD83D\uDD0A `" + suzuyaPlayer.volume + "%`");
        } else {
            playingEmbed.setDescription("\\⏭  `" + endTime + "/" + endTime + "` | \\\uD83D\uDD0A `" + suzuyaPlayer.volume + "%`");
        }
        suzuyaPlayer.editMessage(messageID, playingEmbed.build());
        messageID = null;
        playingEmbed = null;
    }
}
