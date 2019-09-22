package suzuya.events;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.RequestFuture;
import suzuya.util.Captcha;
import suzuya.client.SuzuyaClient;
import suzuya.structures.CaptchaExecutor;
import suzuya.structures.Settings;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class GuildMemberJoin extends ListenerAdapter {
    private final SuzuyaClient suzuya;

    public GuildMemberJoin(SuzuyaClient suzuya) { this.suzuya = suzuya; }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Guild guild = event.getGuild();
        Settings config = suzuya.settingsHandler.getSettings(guild.getId());

        if (config == null) return;
        if (!Boolean.parseBoolean(config.auto_ban) || config.mod_log == null) return;

        TextChannel channel = guild.getTextChannelById(config.mod_log);
        if (channel == null) return;

        User user = event.getUser();

        if (user.isBot()) return;

        Role role = guild.getRoleById(config.silenced_role);

        if (role == null) return;

        suzuya.handleRest(guild.getController().addSingleRoleToMember(guild.getMember(user), role).reason("Unverified User"));

        TextChannel verificationChannel = guild.getTextChannelById(config.verification_channel);

        if (verificationChannel == null) return;

        CaptchaExecutor captcha = new CaptchaExecutor();
        captcha.verificationChannel = verificationChannel;
        captcha.guildID = guild.getId();
        captcha.userID = user.getId();
        captcha.text = Captcha.generateText();

        CompletableFuture<byte[]> generateImage = CompletableFuture.supplyAsync(() -> Captcha.generateImage(captcha.text), this.suzuya.executors);
        generateImage.thenApplyAsync((captchaImage) -> {
            String key = guild.getId() + " " + user.getId();
            RequestFuture<Message> queuedSent = verificationChannel
                    .sendMessage(user.getAsMention() + " | Please verify that you are a human. You have **30 seconds** to answer.")
                    .addFile(captchaImage, "captcha.png")
                    .submit();
            queuedSent.thenApplyAsync((sentMessage) -> {
                captcha.runnable = () -> {
                    suzuya.captcha.remove(key);
                    if (!guild.isMember(user)) return;
                    user.openPrivateChannel()
                            .submit()
                            .thenAcceptAsync((dmChannel) -> suzuya.handleRest(dmChannel.sendMessage("<:hibiki_drink:545882402401157122> You have been kicked from **" + guild.getName() + "** because you failed to complete the verification.")))
                            .whenComplete((ignore, error) -> {
                                suzuya.errorTrace(error.getMessage(), error.getStackTrace());
                                if (suzuya.handleRest(guild.getController().kick(user.getId(), "Failed to complete Captcha. Possible spam bot"))) return;
                                String avatar = suzuya.client.getSelfUser().getAvatarUrl() != null ? suzuya.client.getSelfUser().getAvatarUrl() : suzuya.client.getSelfUser().getDefaultAvatarUrl();
                                MessageEmbed embed = new EmbedBuilder()
                                        .setTitle("📝 | User Kicked")
                                        .setColor(suzuya.defaultEmbedColor)
                                        .setDescription("**• User:** " + user.getAsTag() + " `(" + user.getId() + ")`" + "\n**• Moderator:** " + suzuya.client.getSelfUser().getAsTag() + "\n**• Reason:** <:hibikino:478068372802633739> Failed to complete the Verification.")
                                        .setAuthor(suzuya.client.getSelfUser().getName(), avatar, avatar)
                                        .setTimestamp(Instant.now())
                                        .setFooter(guild.getName(), guild.getIconUrl() != null ? guild.getIconUrl() : avatar)
                                        .build();
                                suzuya.handleRest(channel.sendMessage(embed));
                                suzuya.handleRest(verificationChannel.sendMessage("<:uggh:448387137596030979> **" + user.getAsTag() + "** failed the verification."));
                                suzuya.handleRest(sentMessage.delete());
                            });
                };
                captcha.message = sentMessage;
                captcha.future = suzuya.scheduler.schedule(captcha.runnable, 31, TimeUnit.SECONDS);
                suzuya.captcha.putIfAbsent(key, captcha);
                return null;
            });
            queuedSent.exceptionally((error) -> {
                suzuya.errorTrace(error.getMessage(), error.getStackTrace());
                return null;
            });
            return null;
        });
        generateImage.exceptionally((error) -> {
            suzuya.errorTrace(error.getMessage(), error.getStackTrace());
            return null;
        });
    }
}
