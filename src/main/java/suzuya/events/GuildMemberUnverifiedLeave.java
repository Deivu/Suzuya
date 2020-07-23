package suzuya.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import suzuya.client.SuzuyaClient;
import suzuya.structures.CaptchaExecutor;
import suzuya.structures.Settings;

public class GuildMemberUnverifiedLeave extends ListenerAdapter {
    private final SuzuyaClient suzuya;

    public GuildMemberUnverifiedLeave(SuzuyaClient suzuya) {
        this.suzuya = suzuya;
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        User user = event.getUser();
        Guild guild = event.getGuild();
        Settings config = suzuya.settingsHandler.getSettings(guild.getId());

        if (config == null) return;
        if (!Boolean.parseBoolean(config.auto_ban)) return;
        if (config.verification_channel == null) return;

        String key = guild.getId() + " " + user.getId();
        CaptchaExecutor captcha = this.suzuya.captcha.get(key);

        if (captcha == null) return;

        suzuya.captcha.remove(key);
        captcha.future.cancel(true);
        captcha.message.delete().queue();
    }
}
