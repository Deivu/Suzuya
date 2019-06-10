package suzuya.commands;

import suzuya.player.SuzuyaPlayer;
import suzuya.structures.BaseCommand;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Settings;

public class Stop extends BaseCommand {

    @Override
    public String getTitle() {
        return "stop";
    }

    @Override
    public String getUsage() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "Stops the music playback.";
    }

    @Override
    public String getCategory() {
        return "Moosik";
    }

    @Override
    public String run(HandlerArgs handler, Settings config, String[] args) {
        if (!handler.suzuya.players.containsKey(handler.guild.getId()))
            return "Admiral, " + handler.me.getName() + " wont stop anything if there is no player.";
        if (handler.member.getVoiceState().getChannel() == null)
            return "Admiral, " + handler.me.getName() + " knows you aren't in a voice channel, dummy.";
        SuzuyaPlayer suzuyaPlayer = handler.suzuya.players.get(handler.guild.getId());
        if (!handler.member.getVoiceState().getChannel().getId().equals(suzuyaPlayer.voiceChannel.getId()))
            return "Admiral, " + handler.me.getName() + " won't let you stop anything if you are not in the same voice channel where I am";
        suzuyaPlayer.queue.clear();
        suzuyaPlayer.player.stopTrack();
        return null;
    }
}
