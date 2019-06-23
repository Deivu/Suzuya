package suzuya.events;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import suzuya.Captcha;
import suzuya.SuzuyaClient;
import suzuya.structures.CaptchaExecutor;
import suzuya.structures.Settings;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
        TextChannel channel = guild.getTextChannelById(config.mod_log);
        if (channel == null) return;
        User user = event.getUser();
        if (checkName(user) && lessThanWeek(user)) {
            boolean isExecuted = suzuya.handleRest(guild.getController().ban(user, 7, "Possible Spam Bot Detected"));
            if (!isExecuted) return;
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
            return;
        }
        Role role = guild.getRoleById("438683349217705984"); // Future reimplementation
        if (role != null) {
            suzuya.handleRest(
                    guild.getController().addSingleRoleToMember(guild.getMember(user), role).reason("Temporary Mute for Verification Process.")
            );
        }
        PrivateChannel privateChannel = user.openPrivateChannel().complete();
        CaptchaExecutor captcha = new CaptchaExecutor();
        captcha.guildID = guild.getId();
        captcha.text = Captcha.generateText();
        byte[] data = Captcha.generateImage(captcha.text);
        boolean isExecuted = suzuya.handleRest(
                privateChannel.sendMessage("❓ | **User Verification in " + guild.getName() + "**\nPlease type the Text you see in the picture. You have **20 seconds** to answer.").addFile(data, "data.png")
        );
        if (!isExecuted) return;
        captcha.runnable = () -> {
            suzuya.captcha.remove(user.getId());
            if (!guild.isMember(user)) return;
            boolean _isExecuted = suzuya.handleRest(guild.getController().kick(user.getId(), "Failed to complete Captcha. Possible spam bot"));
            if (!_isExecuted) return;
            String avatar = suzuya.client.getSelfUser().getAvatarUrl() != null ? suzuya.client.getSelfUser().getAvatarUrl() : suzuya.client.getSelfUser().getDefaultAvatarUrl();
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("📝 | User Kicked")
                    .setColor(suzuya.defaultEmbedColor)
                    .setDescription(
                            "**• User:** " + user.getAsTag() + " `(" + user.getId() + ")`" +
                            "\n**• Moderator:** " + suzuya.client.getSelfUser().getAsTag() +
                            "\n**• Reason:** Failed to complete Captcha. Possible spam bot <:lewd:448387419092549632>"
                    )
                    .setAuthor(suzuya.client.getSelfUser().getName(), avatar, avatar)
                    .setTimestamp(Instant.now())
                    .setFooter(guild.getName(), guild.getIconUrl() != null ? guild.getIconUrl() : avatar)
                    .build();
            suzuya.handleRest(channel.sendMessage(embed));
            suzuya.handleRest(privateChannel.sendMessage("<:uggh:448387137596030979> You failed to complete the Verification Process."));
        };
        captcha.future = Executors.newSingleThreadScheduledExecutor().schedule(captcha.runnable, 21, TimeUnit.SECONDS);
        suzuya.captcha.putIfAbsent(user.getId(), captcha);
    }

    private boolean checkName(User user) {
        return pattern.matcher(user.getName()).matches();
    }

    private boolean lessThanWeek(User user) {
        return user.getCreationTime().getSecond() > OffsetDateTime.now().plusDays(7).getSecond();
    }
}
