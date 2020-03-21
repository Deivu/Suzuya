package suzuya.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.VoiceChannel;
import suzuya.player.SuzuyaResolver;
import suzuya.structures.*;
import suzuya.player.SuzuyaPlayer;

import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
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
            return "Admiral, you forgot the query, dummy.";
        VoiceChannel voiceChannel = Objects.requireNonNull(handler.member.getVoiceState()).getChannel();
        if (voiceChannel == null)
            return "Admiral, "+ handler.me.getName() +" knows you aren't in a voice channel, dummy.";
        String query = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        String indentifier = this.verifyURL(query) ? query : "ytsearch:" + query;
        CompletableFuture<Message> sentMessage = handler.channel.sendMessage("Trying to find the query you gave me....").submit();
        // async player loading meme
        sentMessage.thenApplyAsync(message -> {
            CompletableFuture<SuzuyaResult> request = new SuzuyaResolver(handler.suzuya).resolve(indentifier);
            request.thenApplyAsync(data -> {
                try {
                    if (data.result.equals("FAILED")) {
                        message.editMessage("Admiral, seems like I cannot load this query after all.").queue();
                        return null;
                    }

                    if (data.result.equals("NO_MATCHES")) {
                        message.editMessage("Admiral, I didn't find anything on the query you gave. Please try again!").queue();
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
                            SuzuyaPlayerTrack playerTrack = new SuzuyaPlayerTrack(track, handler.member);
                            suzuyaPlayer.queue.offer(playerTrack);
                        }
                        message.editMessage("Loaded the playlist `" + data.playlist + "`. This playlist contains `" + data.tracks.size() + "` track(s).").queue();
                        if (executePlayTrack)
                            suzuyaPlayer.startPlaying(Objects.requireNonNull(suzuyaPlayer.queue.poll()));
                        return null;
                    }

                    AudioTrack track = data.tracks.get(0);
                    SuzuyaPlayerTrack playerTrack = new SuzuyaPlayerTrack(track, handler.member);
                    suzuyaPlayer.queue.offer(playerTrack);

                    message.editMessage("Loaded the track `" + track.getInfo().title + "`").queue();

                    if (executePlayTrack)
                        suzuyaPlayer.startPlaying(Objects.requireNonNull(suzuyaPlayer.queue.poll()));
                } catch (Exception error) {
                    handler.suzuya.util.errorTrace(error.getMessage(), error.getStackTrace());
                }
                return null;
            });
            request.exceptionally((error) -> {
                handler.suzuya.util.errorTrace(error.getMessage(), error.getStackTrace());
                message.editMessage("An error occured when trying to add the track. Error Message:\n`" + error.getMessage() + "`").queue();
                return null;
            });
            return null;
        });
        sentMessage.exceptionally((error) -> {
            handler.suzuya.util.errorTrace(error.getMessage(), error.getStackTrace());
            handler.channel.sendMessage("An error occured when trying to add the track. Error Message:\n`" + error.getMessage() + "`").queue();
            return null;
        });
        return null;
    }

    private  boolean verifyURL(String query)  {
        try {
            new URL(query);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}

