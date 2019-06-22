package suzuya.events;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import suzuya.SuzuyaClient;
import suzuya.structures.Settings;

import java.time.Instant;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class PrivateMessageReceived extends ListenerAdapter {
    private final SuzuyaClient suzuya;
    private final Pattern pattern = Pattern.compile("geniuschat.tk.+");

    public PrivateMessageReceived(SuzuyaClient suzuya) { this.suzuya = suzuya; }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        ArrayList<Settings> settings = suzuya.settingsHandler.getListSettings("auto_ban");
        for (Settings setting : settings) {
            if (!Boolean.parseBoolean(setting.auto_ban)) continue;
            if (!pattern.matcher(event.getMessage().getContentRaw()).find()) continue;
            Guild guild = suzuya.client.getGuildById(setting.guild_id);
            if (guild == null) continue;
            User user = event.getAuthor();
            Member member = guild.getMember(user);
            if (member == null) continue;
            boolean isExecuted = suzuya.handleRest(guild.getController().ban(user, 7, "Possible Spam Bot Detected"));
            if (!isExecuted) continue;
            if (setting.mod_log == null) continue;
            TextChannel channel = guild.getTextChannelById(setting.mod_log);
            if (channel == null) continue;
            String avatar = suzuya.client.getSelfUser().getAvatarUrl() != null ? suzuya.client.getSelfUser().getAvatarUrl() : suzuya.client.getSelfUser().getDefaultAvatarUrl();
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("📝 | User Bannned")
                    .setColor(suzuya.defaultEmbedColor)
                    .setDescription(
                            "**• User:** " + user.getAsTag() + " `(" + user.getId() + ")`" +
                            "\n**• Moderator:** " + suzuya.client.getSelfUser().getAsTag() +
                            "\n**• Reason:** Possible lewd user bot <:lewd:448387419092549632>"
                    )
                    .setAuthor(suzuya.client.getSelfUser().getName(), avatar, avatar)
                    .setTimestamp(Instant.now())
                    .setFooter(guild.getName(), guild.getIconUrl() != null ? guild.getIconUrl() : avatar)
                    .build();
            suzuya.handleRest(channel.sendMessage(embed));
        }
    }
}
