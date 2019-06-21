package suzuya.commands;

import net.dv8tion.jda.core.Permission;
import suzuya.player.SuzuyaPlayer;
import suzuya.structures.BaseCommand;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Settings;

public class Volume extends BaseCommand {

    @Override
    public String getTitle() {
        return "volume";
    }

    @Override
    public String getUsage() {
        return "volume <10-200>";
    }

    @Override
    public String getDescription() {
        return "Changes the playback volume of your playback";
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
            return "Admiral, " + handler.me.getName() + " wont change anything if there is no player.";
        if (handler.member.getVoiceState().getChannel() == null)
            return "Admiral, " + handler.me.getName() + " knows you aren't in a voice channel, dummy.";
        SuzuyaPlayer suzuyaPlayer = handler.suzuya.players.get(handler.guild.getId());
        if (!handler.member.getVoiceState().getChannel().getId().equals(suzuyaPlayer.voiceChannel.getId()))
            return "Admiral, " + handler.me.getName() + " won't let you change anything if you are not in the same voice channel where I am";
        int request;
        try {
            request = Integer.parseInt(args[1]);
            if (request < 10) request = 10;
            if (request > 200) request = 200;
        } catch (Exception error) {
            request = 10;
        }
        handler.channel.sendMessage("Setting the playback volume to **" + request + "%**").queue();
        suzuyaPlayer.setVolume(request);
        return null;
    }
}
