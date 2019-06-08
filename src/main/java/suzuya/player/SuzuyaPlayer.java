package suzuya.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import suzuya.SuzuyaClient;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SuzuyaPlayer {
    public final SuzuyaClient suzuya;
    public final BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();

    public final Guild guild;
    public final AudioPlayer player;
    public final AudioManager audioManager;
    public final VoiceChannel voiceChannel;

    public TextChannel textChannel;

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
        suzuya.players.put(guild.getId(), this);
    }

    public void handleMessage(String message) {
        try {
            if (textChannel == null) return;
            textChannel.sendMessage(message).queue();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void handleMessage(MessageEmbed embed) {
        try {
            if (textChannel == null) return;
            textChannel.sendMessage(embed).queue();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void destroy() {
        player.destroy();
        audioManager.closeAudioConnection();
        suzuya.players.remove(guild.getId());
    }
}
