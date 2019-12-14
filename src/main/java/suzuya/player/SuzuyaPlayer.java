package suzuya.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.AudioManager;
import net.dv8tion.jda.core.requests.RequestFuture;
import suzuya.client.SuzuyaClient;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SuzuyaPlayer {
    public final SuzuyaClient suzuya;
    public final BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();
    public final AudioPlayer player;
    public final VoiceChannel voiceChannel;

    private final Guild guild;
    private final AudioManager audioManager;
    private final TextChannel textChannel;

    public int volume = 50;
    public AudioTrack currentTrack = null;

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

    public void setVolume(int volume) {
        this.volume = volume;
        player.setVolume(this.volume);
    }

    void handleMessage(MessageEmbed embed) {
        if (textChannel == null) return;
        textChannel.sendMessage(embed).queue();
    }

    RequestFuture<Message> handleMessageFuture(MessageEmbed embed) {
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
}
