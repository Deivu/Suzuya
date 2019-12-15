package suzuya.commands;

import net.dv8tion.jda.api.Permission;
import suzuya.player.SuzuyaPlayer;
import suzuya.structures.BaseCommand;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Settings;

import java.util.Objects;

public class Skip extends BaseCommand {

    @Override
    public String getTitle() {
        return "skip";
    }

    @Override
    public String getUsage() {
        return "skip";
    }

    @Override
    public String getDescription() {
        return "Skips the track you specified";
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
            return "Admiral, " + handler.me.getName() + " wont skip anything if there is nothing to skip.";
        if (Objects.requireNonNull(handler.member.getVoiceState()).getChannel() == null)
            return "Admiral, " + handler.me.getName() + " knows you aren't in a voice channel, dummy.";
        SuzuyaPlayer suzuyaPlayer = handler.suzuya.players.get(handler.guild.getId());
        if (!Objects.requireNonNull(handler.member.getVoiceState().getChannel()).getId().equals(suzuyaPlayer.voiceChannel.getId()))
            return "Admiral, " + handler.me.getName() + " won't let you skip anything if you are not in the same voice channel where I am";
        handler.channel.sendMessage("Skipping the currently playing track.").queue();
        suzuyaPlayer.player.stopTrack();
        return null;
    }
}
