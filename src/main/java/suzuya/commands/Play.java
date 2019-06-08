package suzuya.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.apache.commons.lang3.StringUtils;
import suzuya.player.SuzuyaResolver;
import suzuya.structures.BaseCommand;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Settings;
import suzuya.player.SuzuyaPlayer;
import suzuya.structures.SuzuyaTrack;

import java.util.concurrent.CompletableFuture;

public class Play extends BaseCommand {

    @Override
    public String getTitle() {
        return "play";
    }

    @Override
    public String getUsage() {
        return "play <source>";
    }

    @Override
    public String getDescription() {
        return "Plays the track you specified";
    }

    @Override
    public String getCategory() {
        return "Moosik";
    }

    @Override
    public String run(HandlerArgs handler, Settings config, String[] args) {
        if (args[1] == null)
            return "Admiral, you forgot the link, dummy.";
        VoiceChannel voiceChannel = handler.member.getVoiceState().getChannel();
        if (voiceChannel == null)
            return "Admiral, you must be in a voice channel to execute this command";
        String url = args[1];
        if (handler.suzuya.players.containsKey(handler.guild.getId())) {
            new SuzuyaResolver(handler.suzuya.PlayerManager).resolve(url)
                    .thenApply(res -> {
                        if (res.result.equals("NO_MATCHES") || res.result.equals("FAILED")) {
                            handler.channel.sendMessage("Admiral, seems like I cannot load this track after all.").queue();
                            return null;
                        }
                        SuzuyaPlayer suzuyaPlayer = handler.suzuya.players.get(handler.guild.getId());
                        if (res.result.equals("PLAYLIST")) {
                            for (AudioTrack track : res.tracks) {
                                suzuyaPlayer.queue.offer(track);
                            }
                            suzuyaPlayer.handleMessage("Added the playlist **" + res.playlist + "** to the queue");
                            return null;
                        }
                        AudioTrack _track = res.tracks.get(0);
                        suzuyaPlayer.queue.offer(_track);
                        suzuyaPlayer.handleMessage("Added the track **" + _track.getInfo().title + "** to the queue");
                        return null;
                    });
        }
        new SuzuyaResolver(handler.suzuya.PlayerManager).resolve(url)
                .thenApply(res -> {
                    if (res.result.equals("NO_MATCHES") || res.result.equals("FAILED")) {
                        handler.channel.sendMessage("Admiral, seems like I cannot load this track after all.").queue();
                        return null;
                    }
                    SuzuyaPlayer suzuyaPlayer = new SuzuyaPlayer(handler.suzuya, handler.channel, voiceChannel);
                    if (res.result.equals("PLAYLIST")) {
                        for (AudioTrack track : res.tracks) {
                            suzuyaPlayer.queue.offer(track);
                        }
                        suzuyaPlayer.player.playTrack(suzuyaPlayer.queue.poll());
                        suzuyaPlayer.handleMessage("Added the playlist **" + res.playlist + "** to the queue");
                        return null;
                    }
                    AudioTrack _track = res.tracks.get(0);
                    suzuyaPlayer.queue.offer(_track);
                    suzuyaPlayer.player.playTrack(suzuyaPlayer.queue.poll());
                    suzuyaPlayer.handleMessage("Added the track **" + _track.getInfo().title + "** to the queue");
                    return null;
                });
        return null;
    }
}

