package suzuya.structures;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

public class SuzuyaPlayerTrack {
    public final AudioTrack track;
    private final Member member;

    public SuzuyaPlayerTrack(AudioTrack track, Member member) {
        this.track = track;
        this.member = member;
    }

    public boolean hasNoPermissionForAction(Member invoker) {
        return !invoker.getId().equals(member.getId()) && !invoker.hasPermission(Permission.PRIORITY_SPEAKER);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isSuperUser(Member invoker) {
        return invoker.hasPermission(Permission.PRIORITY_SPEAKER);
    }
}
