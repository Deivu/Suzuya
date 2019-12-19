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

    public boolean hasNoPermissionForAction(Member member) {
        return !member.getId().equals(this.member.getId()) && !member.hasPermission(Permission.PRIORITY_SPEAKER);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isSuperUser(Member member) {
        return member.hasPermission(Permission.PRIORITY_SPEAKER);
    }
}
