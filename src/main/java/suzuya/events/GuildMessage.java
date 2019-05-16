package suzuya.events;

import net.dv8tion.jda.core.entities.*;
import suzuya.SuzuyaClient;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.EmbedBuilder;
import suzuya.structures.BaseCommand;
import suzuya.structures.Tag;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Settings;

public class GuildMessage extends ListenerAdapter {
    private final SuzuyaClient suzuya;

    public GuildMessage(SuzuyaClient _suzuya) { suzuya = _suzuya; }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (!suzuya.isClientReady || event.isWebhookMessage()) return;
        HandlerArgs handler = new HandlerArgs(suzuya, event);
        if (handler.author.isBot()) return;
        Settings config = suzuya.settingsHandler.getSettings(handler.guild.getId());
        String content = handler.msg.getContentRaw();
        if (content.startsWith(config.prefix)) {
            String[] args = content.split("\\s+");
            String query = args[0].replace(config.prefix, "").toLowerCase();
            BaseCommand command = suzuya.commandHandler.getCommand(query);
            if (command != null) {
                try {
                    String response = command.run(handler, config, args);
                    if (response != null) handler.channel.sendMessage(response).queue();
                } catch (Exception error) {
                    SelfUser me = suzuya.client.getSelfUser();
                    MessageEmbed embed = new EmbedBuilder()
                            .setColor(suzuya.defaultEmbedColor)
                            .addField("This command raised an error...", "```java\n" + error.toString() + "```", false)
                            .addField("Goumen Admiral...", "You shouldn't be receiving this... unless you are doing something wrong", false)
                            .setFooter("Command Name: " + command.getTitle(), me.getAvatarUrl() != null ? me.getAvatarUrl() : me.getDefaultAvatarUrl())
                            .build();
                    handler.channel.sendMessage(embed).queue();
                    error.printStackTrace();
                }
            } else {
                Tag tag = suzuya.tagsHandler.getTag(handler.guild.getId(), query);
                if (tag == null) return;
                handler.channel.sendMessage(tag.content).queue();
            }
        }
    }
}
