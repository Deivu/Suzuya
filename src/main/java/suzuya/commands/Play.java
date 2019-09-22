package suzuya.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.requests.RequestFuture;
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
        RequestFuture<Message> sentMessage = handler.channel.sendMessage("Trying to find the query you gave me....").submit();
        sentMessage.thenApplyAsync(message -> {
            CompletableFuture<SuzuyaTrack> request = new SuzuyaResolver(handler.suzuya).resolve(url);
            request.thenApplyAsync(data -> {
                try {
                    if (data.result.equals("NO_MATCHES") || data.result.equals("FAILED")) {
                        handler.suzuya.handleRest(message.editMessage("Admiral, seems like I cannot load this track after all."));
                        return null;
                    }

                    SuzuyaPlayer suzuyaPlayer = handler.suzuya.players.get(handler.guild.getId());
                    boolean executePlayTrack = false;
                    if (suzuyaPlayer == null) {
                        suzuyaPlayer = new SuzuyaPlayer(handler.suzuya, handler.channel, voiceChannel);
                        executePlayTrack = true;
                    }

                    if (data.result.equals("PLAYLIST")) {
                        for (AudioTrack track : data.tracks) {
                            suzuyaPlayer.queue.offer(track);
                        }
                        handler.suzuya.handleRest(message.editMessage("Loaded the playlist `" + data.playlist + "`"));
                        if (executePlayTrack)
                            suzuyaPlayer.player.playTrack(suzuyaPlayer.queue.poll());
                        return null;
                    }

                    AudioTrack track = data.tracks.get(0);
                    suzuyaPlayer.queue.offer(track);
                    handler.suzuya.handleRest(message.editMessage("Loaded the track `" + track.getInfo().title + "`"));
                    if (executePlayTrack)
                        suzuyaPlayer.player.playTrack(suzuyaPlayer.queue.poll());
                } catch (Exception error) {
                    handler.suzuya.errorTrace(error.getMessage(), error.getStackTrace());
                }
                return null;
            });
            request.exceptionally((error) -> {
                handler.suzuya.errorTrace(error.getMessage(), error.getStackTrace());
                handler.suzuya.handleRest(message.editMessage("An error occured when trying to add the track. Error Message:\n`" + error.getMessage() + "`"));
                return null;
            });
            return null;
        });
        sentMessage.exceptionally((error) -> {
            handler.suzuya.errorTrace(error.getMessage(), error.getStackTrace());
            handler.suzuya.handleRest(handler.channel.sendMessage("An error occured when trying to add the track. Error Message:\n`" + error.getMessage() + "`"));
            return null;
        });
        return null;
    }
}

