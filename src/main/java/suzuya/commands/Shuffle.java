package suzuya.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import suzuya.player.SuzuyaPlayer;
import suzuya.structures.BaseCommand;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Settings;

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
    public String run(HandlerArgs handler, Settings config, String[] args) {
        if (!handler.suzuya.players.containsKey(handler.guild.getId()))
            return "Admiral, Suzuya wont shuffle anything if there is nothing to skip.";
        if (handler.member.getVoiceState().getChannel() == null)
            return "Admiral, Suzuya knows you aren't in a voice channel, dummy.";
        SuzuyaPlayer suzuyaPlayer = handler.suzuya.players.get(handler.guild.getId());
        if (!handler.member.getVoiceState().getChannel().getId().equals(suzuyaPlayer.voiceChannel.getId()))
            return "Admiral, Suzuya won't let you shuffle anything if you are not in the same voice channel where I am";
        List<AudioTrack> list = new ArrayList<>();
        suzuyaPlayer.queue.drainTo(list);
        Collections.shuffle(list);
        for (AudioTrack track: list) suzuyaPlayer.queue.offer(track);
        return "Admiral, Suzuya shuffled your queue, I deserve some head pat for that.";
    }
}
