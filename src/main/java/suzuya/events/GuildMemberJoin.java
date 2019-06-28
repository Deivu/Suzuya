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
    private final Pattern pattern = Pattern.compile("([A-Z][a-z]+[0-9]{1,4}|[A-Z][a-z]+\\.([a-z]+\\.[a-z]+|[a-z]+[0-9]{1,2}))");

    public GuildMemberJoin(SuzuyaClient suzuya) { this.suzuya = suzuya; }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Guild guild = event.getGuild();
        Settings config = suzuya.settingsHandler.getSettings(guild.getId());
        if (!Boolean.parseBoolean(config.auto_ban) || config.mod_log == null) return;
        TextChannel channel = guild.getTextChannelById(config.mod_log);
        if (channel == null) return;
        User user = event.getUser();
        if (user.isBot()) return;

        if (checkName(user) && lessThanWeek(user)) {
            boolean isExecuted = suzuya.handleRest(guild.getController().kick(
                    user.getId(),
                    "Possible Spam Bot Detected")
            );
            if (!isExecuted) return;
            String avatar = suzuya.client.getSelfUser().getAvatarUrl() != null ? suzuya.client.getSelfUser().getAvatarUrl() : suzuya.client.getSelfUser().getDefaultAvatarUrl();
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("📝 | User Kicked")
                    .setColor(suzuya.defaultEmbedColor)
                    .setDescription(
                            "**• User:** " + user.getAsTag() + " `(" + user.getId() + ")`" +
                            "\n**• Moderator:** " + suzuya.client.getSelfUser().getAsTag() +
                            "\n**• Reason:** <:lewd:448387419092549632> Possible lewd user bot"
                    )
                    .setAuthor(suzuya.client.getSelfUser().getName(), avatar, avatar)
                    .setTimestamp(Instant.now())
                    .setFooter(guild.getName(), guild.getIconUrl() != null ? guild.getIconUrl() : avatar)
                    .build();
            suzuya.handleRest(channel.sendMessage(embed));
            return;
        }

        Role role = guild.getRoleById(config.silenced_role);
        if (role != null) {
            suzuya.handleRest(
                    guild.getController().addSingleRoleToMember(
                            guild.getMember(user),
                            role
                    ).reason("Unverified User")
            );
        }

        TextChannel verificationChannel = guild.getTextChannelById(config.verification_channel);
        if (verificationChannel == null) return;

        CaptchaExecutor captcha = new CaptchaExecutor();
        captcha.verificationChannel = verificationChannel;
        captcha.guildID = guild.getId();
        captcha.userID = user.getId();
        captcha.text = Captcha.generateText();
        byte[] data = Captcha.generateImage(captcha.text);
        String key = guild.getId() + " " + user.getId();

        verificationChannel.sendMessage(user.getAsMention() + " | Please verify that you are a human. You have **20 seconds** to answer.")
                .addFile(data, "data.png")
                .submit()
                .thenApply(res -> {
                    captcha.runnable = () -> {
                        suzuya.captcha.remove(key);
                        if (!guild.isMember(user)) return;
                        user.openPrivateChannel()
                                .submit()
                                .thenApply(dm -> {
                                    suzuya.handleRest(
                                            dm.sendMessage("<:hibiki_drink:545882402401157122> You have been kicked from **" + guild.getName() + "** because you failed to complete the verification.")
                                    );
                                    return null;
                                })
                                .exceptionally(error -> {
                                    suzuya.errorTrace(error.getMessage(), error.getStackTrace());
                                    return null;
                                });
                        boolean _isExecuted = suzuya.handleRest(
                                guild.getController().kick(
                                        user.getId(),
                                        "Failed to complete Captcha. Possible spam bot"
                                )
                        );
                        if (!_isExecuted) return;
                        String avatar = suzuya.client.getSelfUser().getAvatarUrl() != null ? suzuya.client.getSelfUser().getAvatarUrl() : suzuya.client.getSelfUser().getDefaultAvatarUrl();
                        MessageEmbed embed = new EmbedBuilder()
                                .setTitle("📝 | User Kicked")
                                .setColor(suzuya.defaultEmbedColor)
                                .setDescription(
                                        "**• User:** " + user.getAsTag() + " `(" + user.getId() + ")`" +
                                        "\n**• Moderator:** " + suzuya.client.getSelfUser().getAsTag() +
                                        "\n**• Reason:** <:hibikino:478068372802633739> Failed to complete the Verification."
                                )
                                .setAuthor(suzuya.client.getSelfUser().getName(), avatar, avatar)
                                .setTimestamp(Instant.now())
                                .setFooter(guild.getName(), guild.getIconUrl() != null ? guild.getIconUrl() : avatar)
                                .build();
                        suzuya.handleRest(channel.sendMessage(embed));

                        suzuya.handleRest(verificationChannel.sendMessage("<:uggh:448387137596030979> **" + user.getAsTag() + "** failed the verification."));
                        suzuya.handleRest(res.delete());
                    };

                    captcha.message = res;
                    captcha.future = Executors.newSingleThreadScheduledExecutor().schedule(captcha.runnable, 21, TimeUnit.SECONDS);

                    suzuya.captcha.putIfAbsent(key, captcha);
                    return null;
                })
                .exceptionally(error -> {
                    suzuya.captcha.remove(key);
                    suzuya.errorTrace(error.getMessage(), error.getStackTrace());
                    return null;
                });
    }

    private boolean checkName(User user) {
        return pattern.matcher(user.getName()).matches();
    }

    private boolean lessThanWeek(User user) {
        return user.getCreationTime().getSecond() > OffsetDateTime.now().plusDays(7).getSecond();
    }
}
