package suzuya.events;

import net.dv8tion.jda.core.entities.*;
import suzuya.SuzuyaClient;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.EmbedBuilder;
import suzuya.structures.BaseCommand;
import suzuya.structures.Tag;

public class GuildMessage extends ListenerAdapter {
    private final SuzuyaClient suzuya;

    public GuildMessage(SuzuyaClient _suzuya) { suzuya = _suzuya; }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (!suzuya.isClientReady || event.isWebhookMessage()) return;
        User author = event.getAuthor();
        if (author.isBot()) return;
        Message msg = event.getMessage();
        String content = msg.getContentRaw();
        Member member = event.getMember();
        Guild guild = event.getGuild();
        String prefix = suzuya.settingsHandler.getDataString("prefix", guild.getId());
        if (content.startsWith(prefix)) {
            MessageChannel channel = msg.getChannel();
            String[] args = msg.getContentRaw().split("\\s+");
            String query = args[0].replace(prefix, "").toLowerCase();
            BaseCommand command = suzuya.commandHandler.getCommand(query);
            if (command != null) {
                try {
                    String response = command.run(suzuya, msg, guild, author, member, channel, args);
                    if (response != null) channel.sendMessage(response).queue();
                } catch (Exception error) {
                    SelfUser me = suzuya.client.getSelfUser();
                    MessageEmbed embed = new EmbedBuilder()
                            .setColor(suzuya.defaultEmbedColor)
                            .addField("This command raised an error...", "```java\n" + error.toString() + "```", false)
                            .addField("Goumen Admiral...", "You shouldn't be receiving this... unless you are doing something wrong", false)
                            .setFooter("Command Name: " + command.getTitle(), me.getAvatarUrl() != null ? me.getAvatarUrl() : me.getDefaultAvatarUrl())
                            .build();
                    channel.sendMessage(embed).queue();
                    error.printStackTrace();
                }
            } else {
                Tag tag = suzuya.tagsHandler.getTag(guild.getId(), query);
                if (tag == null) return;
                channel.sendMessage(tag.content).queue();
            }
        }
    }
}
