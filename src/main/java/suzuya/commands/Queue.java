package suzuya.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.StringUtils;
import suzuya.player.SuzuyaPlayer;
import suzuya.structures.BaseCommand;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Page;
import suzuya.structures.Settings;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Queue extends BaseCommand {

    private int number = 0;

    @Override
    public String getTitle() {
        return "queue";
    }

    @Override
    public String getUsage() {
        return "queue <page>";
    }

    @Override
    public String getDescription() {
        return "Shows the queued songs in your guild.";
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
        if (!handler.suzuya.players.containsKey(handler.guild.getId()))
            return "Admiral,  " + handler.me.getName() + "  can't show anything if there is no player.";
        if (Objects.requireNonNull(handler.member.getVoiceState()).getChannel() == null)
            return "Admiral, " + handler.me.getName() + " knows you aren't in a voice channel, dummy.";
        SuzuyaPlayer suzuyaPlayer = handler.suzuya.players.get(handler.guild.getId());
        if (!Objects.requireNonNull(handler.member.getVoiceState().getChannel()).getId().equals(suzuyaPlayer.voiceChannel.getId()))
            return "Admiral, " + handler.me.getName() + " won't let you see anything if you are not in the same voice channel where I am";
        int request;
        try {
            request = Integer.parseInt(args[1]);
            if (request < 0) request = 1;
        } catch (Exception error) {
            request = 1;
        }
        Page data = handler.suzuya.util.paginate(suzuyaPlayer.queue.size(), request, 10);
        this.number = data.start;
        List<String> tracks = Arrays.stream(Arrays.copyOfRange(suzuyaPlayer.queue.toArray(new suzuya.structures.SuzuyaPlayerTrack[0]), data.start, data.end))
                .map(val -> {
                    increment();
                    return "**" + number + ".** " + val.track.getInfo().title;
                })
                .collect(Collectors.toList());
        MessageEmbed embed = new EmbedBuilder()
                .setColor(handler.suzuya.defaultEmbedColor)
                .setThumbnail(handler.me.getAvatarUrl() != null ? handler.me.getAvatarUrl() : handler.me.getDefaultAvatarUrl())
                .setTitle("\\▶ Now Playing")
                .setDescription("**" + suzuyaPlayer.player.getPlayingTrack().getInfo().title + "**")
                .addField("• Queued Songs", StringUtils.join(tracks, "\n"), false)
                .setFooter(data.current + " / " + data.max + " page(s) | " + suzuyaPlayer.queue.size() + " songs in queue.", handler.me.getAvatarUrl() != null ? handler.me.getAvatarUrl() : handler.me.getDefaultAvatarUrl())
                .build();
        handler.channel.sendMessage(embed).queue();
        return null;
    }

    private void increment() {
        number++;
    }
}
