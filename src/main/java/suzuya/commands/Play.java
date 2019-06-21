package suzuya.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.VoiceChannel;
import suzuya.player.SuzuyaResolver;
import suzuya.structures.BaseCommand;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Settings;
import suzuya.player.SuzuyaPlayer;

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
    public boolean ownerOnly() { return false; }

    @Override
    public Permission[] getPermissions() {
        return null;
    }

    @Override
    public String run(HandlerArgs handler, Settings config, String[] args) {
        if (args.length <= 1)
            return "Admiral, you forgot the link, dummy.";
        VoiceChannel voiceChannel = handler.member.getVoiceState().getChannel();
        if (voiceChannel == null)
            return "Admiral, "+ handler.me.getName() +" knows you aren't in a voice channel, dummy.";
        String url = args[1];
        if (handler.suzuya.players.containsKey(handler.guild.getId())) {
            new SuzuyaResolver(handler.suzuya).resolve(url)
                    .thenApply(res -> {
                        if (res.result.equals("NO_MATCHES") || res.result.equals("FAILED")) {
                            handler.suzuya.handleRest(handler.channel.sendMessage("Admiral, seems like I cannot load this track after all."));
                            return null;
                        }
                        SuzuyaPlayer suzuyaPlayer = handler.suzuya.players.get(handler.guild.getId());
                        if (res.result.equals("PLAYLIST")) {
                            for (AudioTrack track : res.tracks) {
                                suzuyaPlayer.queue.offer(track);
                            }
                            handler.suzuya.handleRest(handler.channel.sendMessage("Added the playlist **" + res.playlist + "** to the queue"));
                            return null;
                        }
                        AudioTrack _track = res.tracks.get(0);
                        suzuyaPlayer.queue.offer(_track);
                        handler.suzuya.handleRest(handler.channel.sendMessage("Added the track **" + _track.getInfo().title + "** to the queue"));
                        return null;
                    });
            return null;
        }
        new SuzuyaResolver(handler.suzuya).resolve(url)
                .thenApply(res -> {
                    if (res.result.equals("NO_MATCHES") || res.result.equals("FAILED")) {
                        handler.suzuya.handleRest(handler.channel.sendMessage("Admiral, seems like I cannot load this track after all."));
                        return null;
                    }
                    SuzuyaPlayer suzuyaPlayer = new SuzuyaPlayer(handler.suzuya, handler.channel, voiceChannel);
                    if (res.result.equals("PLAYLIST")) {
                        for (AudioTrack track : res.tracks) {
                            suzuyaPlayer.queue.offer(track);
                        }
                        suzuyaPlayer.player.playTrack(suzuyaPlayer.queue.poll());
                        handler.suzuya.handleRest(handler.channel.sendMessage("Added the playlist **" + res.playlist + "** to the queue"));
                        return null;
                    }
                    AudioTrack _track = res.tracks.get(0);
                    suzuyaPlayer.queue.offer(_track);
                    suzuyaPlayer.player.playTrack(suzuyaPlayer.queue.poll());
                    handler.suzuya.handleRest(handler.channel.sendMessage("Added the track **" + _track.getInfo().title + "** to the queue"));
                    return null;
                });
        return null;
    }
}

