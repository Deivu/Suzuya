package suzuya.commands;

import net.dv8tion.jda.api.Permission;
import suzuya.player.SuzuyaPlayer;
import suzuya.structures.BaseCommand;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Settings;
import suzuya.structures.SuzuyaPlayerTrack;

import java.util.*;

public class Shuffle extends BaseCommand {

    @Override
    public String getTitle() {
        return "shuffle";
    }

    @Override
    public String getUsage() {
        return "shuffle";
    }

    @Override
    public String getDescription() {
        return "Shuffles your queue";
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
            return "Admiral, " + handler.me.getName() + " wont shuffle anything if there is nothing to skip.";
        if (Objects.requireNonNull(handler.member.getVoiceState()).getChannel() == null)
            return "Admiral, " + handler.me.getName() + " knows you aren't in a voice channel, dummy.";
        SuzuyaPlayer suzuyaPlayer = handler.suzuya.players.get(handler.guild.getId());
        if (!Objects.requireNonNull(handler.member.getVoiceState().getChannel()).getId().equals(suzuyaPlayer.voiceChannel.getId()))
            return "Admiral, " + handler.me.getName() + " won't let you shuffle anything if you are not in the same voice channel where I am";
        if (suzuyaPlayer.queue.size() < 2)
            return "Admiral, there is no point on trying to shuffle a queue that contains less than 2 songs.";
        if (!suzuyaPlayer.currentTrack.isSuperUser(handler.member))
            return "Admiral, " + handler.me.getName() + " won't let you shuffle anything if you don't have enough permissions to do so.";
        List<SuzuyaPlayerTrack> list = new ArrayList<>();
        suzuyaPlayer.queue.drainTo(list);
        Collections.shuffle(list);
        for (SuzuyaPlayerTrack track: list) {
            suzuyaPlayer.queue.offer(track);
        }
        return "Admiral, " + handler.me.getName() + " shuffled your queue, I deserve some head pat for that.";
    }
}
