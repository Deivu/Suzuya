package suzuya.events;

import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import suzuya.SuzuyaClient;
import suzuya.structures.CaptchaExecutor;
import suzuya.structures.Settings;

public class PrivateMessageReceived extends ListenerAdapter {
    private final SuzuyaClient suzuya;

    public PrivateMessageReceived(SuzuyaClient suzuya) { this.suzuya = suzuya; }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        User user = event.getAuthor();
        if (!suzuya.captcha.containsKey(user.getId())) return;
        CaptchaExecutor captcha = suzuya.captcha.get(user.getId());
        Settings config = suzuya.settingsHandler.getSettings(captcha.guildID);
        if (!Boolean.parseBoolean(config.auto_ban)) return;
        Guild guild = suzuya.client.getGuildById(captcha.guildID);
        if (guild == null) return;
        Member member = guild.getMember(user);
        if (member == null) return;
        PrivateChannel privateChannel = user.openPrivateChannel().complete();
        if (!captcha.text.equals(event.getMessage().getContentRaw())) {
            suzuya.handleRest(privateChannel.sendMessage("<:hibikino:478068372802633739> Admiral, wrong guess on captcha. Try again."));
            return;
        }
        captcha.future.cancel(true);
        Role role = guild.getRoleById("438683349217705984"); // Future reimplementation
        if (role != null) {
            suzuya.handleRest(
                    guild.getController().removeSingleRoleFromMember(member, role).reason("Completed the Verification Process.")
            );
        }
        suzuya.captcha.remove(user.getId());
        suzuya.handleRest(privateChannel.sendMessage("<:hibikiyes:478068379832549383> Yay! Admiral, you now have access in **" + guild.getName() + "**"));
    }
}
