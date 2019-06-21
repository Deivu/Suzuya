package suzuya.events;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import suzuya.SuzuyaClient;
import suzuya.structures.Settings;

import java.time.Instant;
import java.util.regex.Pattern;
import java.time.OffsetDateTime;

public class GuildMemberJoin extends ListenerAdapter {
    private final SuzuyaClient suzuya;
    private Pattern pattern = Pattern.compile("([A-Z][a-z]+[0-9]{1,4}|[A-Z][a-z]+\\.([a-z]+\\.[a-z]+|[a-z]+[0-9]{1,2}))");

    public GuildMemberJoin(SuzuyaClient suzuya) { this.suzuya = suzuya; }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Guild guild = event.getGuild();
        Settings config = suzuya.settingsHandler.getSettings(guild.getId());
        if (!Boolean.parseBoolean(config.auto_ban) || config.mod_log == null) return;
        TextChannel channel = guild.getTextChannelCache().getElementById(config.mod_log);
        if (channel == null) return;
        User user = event.getUser();
        boolean isSpambot = checkSpamBot(user);
        if (!isSpambot) return;
        boolean isExecuted = suzuya.handleRest(event.getGuild().getController().ban(user, 7, "Possible Spam Bot Detected"));
        if (!isExecuted) return;
        String avatar = suzuya.client.getSelfUser().getAvatarUrl() != null ? suzuya.client.getSelfUser().getAvatarUrl() : suzuya.client.getSelfUser().getDefaultAvatarUrl();
        MessageEmbed embed = new EmbedBuilder()
                .setTitle("• User Bannned")
                .setColor(suzuya.defaultEmbedColor)
                .setDescription("Reason: <:eww:544402708975583243> Possible Lewd User Bot.")
                .setAuthor(suzuya.client.getSelfUser().getName(), avatar)
                .addField("• User", user.getAsTag() + "(" + user.getId() + ")", true)
                .addField("• Moderator", suzuya.client.getSelfUser().getAsTag(), true)
                .setTimestamp(Instant.now())
                .setFooter(guild.getName(), guild.getIconUrl() != null ? guild.getIconUrl() : avatar)
                .build();
        suzuya.handleRest(channel.sendMessage(embed));
    }

    private boolean checkSpamBot(User user) {
        boolean matches = pattern.matcher(user.getName()).matches();
        return matches && user.getCreationTime().getSecond() > OffsetDateTime.now().plusDays(7).getSecond();
    }
}
