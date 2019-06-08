package suzuya.commands;

import net.dv8tion.jda.core.entities.VoiceChannel;
import org.apache.commons.lang3.StringUtils;
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
    public String run(HandlerArgs handler, Settings config, String[] args) {
        if (args[1] == null)
            return "Admiral, you forgot the link, dummy.";
        VoiceChannel voiceChannel = handler.member.getVoiceState().getChannel();
        if (voiceChannel == null)
            return "Admiral, you must be in a voice channel to execute this command";
        String url = StringUtils.join(args[1], " ");
        if (handler.suzuya.players.containsKey(handler.guild.getId())) {
            SuzuyaPlayer suzuyaPlayer = handler.suzuya.players.get(handler.guild.getId());
            suzuyaPlayer.actuallyResolveThing(url);
            return null;
        }
        SuzuyaPlayer suzuyaPlayer = new SuzuyaPlayer(handler.suzuya, handler.channel, voiceChannel);
        suzuyaPlayer.actuallyResolveThing(url);
        return null;
    }
}

