package suzuya.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import suzuya.client.SuzuyaClient;
import suzuya.structures.CaptchaExecutor;
import suzuya.structures.Settings;

import java.util.concurrent.TimeUnit;

public class GuildVerificationMessage extends ListenerAdapter {
    private final SuzuyaClient suzuya;

    public GuildVerificationMessage(SuzuyaClient suzuya) {
        this.suzuya = suzuya;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        User user = event.getAuthor();
        Guild guild = event.getGuild();
        Settings config = suzuya.settingsHandler.getSettings(guild.getId());

        if (config == null) return;
        if (!Boolean.parseBoolean(config.auto_ban)) return;
        if (config.verification_channel == null) return;
        if (!event.getChannel().getId().equals(config.verification_channel)) return;

        String key = guild.getId() + " " + user.getId();

        CaptchaExecutor captcha = this.suzuya.captcha.get(key);

        if (captcha == null) return;

        if (!captcha.text.equals(event.getMessage().getContentRaw())) {
            captcha.verificationChannel
                    .sendMessage("<:hibiki_drink:545882402401157122> Admiral **" + user.getAsTag() + "** wrong guess. Try again.")
                    .submit()
                    .handleAsync((res, error) -> {
                        if (error != null) {
                            suzuya.errorTrace(error.getMessage(), error.getStackTrace());
                            return null;
                        }
                        event.getMessage().delete().queue();
                        suzuya.scheduler.schedule(() -> res.delete().queue(), 3, TimeUnit.SECONDS);
                        return null;
                    });
            return;
        }

        this.suzuya.captcha.remove(key);

        captcha.future.cancel(true);

        Member member = event.getMember();

        if (member == null) return;

        Role role = guild.getRoleById(config.silenced_role);
        if (role != null) {
            guild.removeRoleFromMember(member, role).reason("Completed the verification.").queue();
        }

        captcha.message.delete().queue();
        event.getMessage().delete().queue();
        captcha.verificationChannel.sendMessage("<:uzuki_pyon:545889147211218945> Admiral **" + user.getAsTag() + "**, now have access to the guild.").queue();

        user.openPrivateChannel()
                .submit()
                .handleAsync((res, error) -> {
                    if (error != null) {
                        suzuya.errorTrace(error.getMessage(), error.getStackTrace());
                        return null;
                    }
                    res.sendMessage("<:uzuki_pyon:545889147211218945> Yay! Admiral **" + user.getAsTag() + "**, you now have access to the **" + guild.getName() + "**").queue();
                    return null;
                });
    }
}
