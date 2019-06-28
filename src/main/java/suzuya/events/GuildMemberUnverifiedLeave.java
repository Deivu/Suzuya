package suzuya.events;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import suzuya.SuzuyaClient;
import suzuya.structures.CaptchaExecutor;
import suzuya.structures.Settings;

public class GuildMemberUnverifiedLeave extends ListenerAdapter {
    private final SuzuyaClient suzuya;

    public GuildMemberUnverifiedLeave(SuzuyaClient suzuya) {
        this.suzuya = suzuya;
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        User user = event.getUser();
        Guild guild = event.getGuild();
        Settings config = suzuya.settingsHandler.getSettings(guild.getId());

        if (!Boolean.parseBoolean(config.auto_ban)) return;
        if (config.verification_channel == null) return;

        String key = guild.getId() + " " + user.getId();
        CaptchaExecutor captcha = this.suzuya.captcha.get(key);
        if (captcha == null) return;

        suzuya.handleRest(captcha.message.delete());
        suzuya.captcha.remove(key);
    }
}
