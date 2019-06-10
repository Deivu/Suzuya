package suzuya.events;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import suzuya.SuzuyaClient;
import suzuya.structures.BaseCommand;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Settings;

public class GuildMessage extends ListenerAdapter {
    private final SuzuyaClient suzuya;

    public GuildMessage(SuzuyaClient suzuya) {
        this.suzuya = suzuya;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (!suzuya.isClientReady || event.isWebhookMessage()) return;
        HandlerArgs handler = new HandlerArgs(suzuya, event);
        if (handler.author.isBot()) return;
        Settings config = suzuya.settingsHandler.getSettings(handler.guild.getId());
        String content = handler.msg.getContentRaw();
        if (content.startsWith(config.prefix)) {
            String[] args = content.split("\\s+");
            String query = args[0].substring(config.prefix.length());
            BaseCommand command = suzuya.commandHandler.getCommand(query);
            if (command != null) {
                try {
                    String response = command.run(handler, config, args);
                    if (response != null) handler.channel.sendMessage(response).queue();
                } catch (Exception error) {
                    MessageEmbed embed = new EmbedBuilder()
                            .setColor(suzuya.defaultEmbedColor)
                            .addField("This command raised an error...", "```java\n" + error.toString() + "```", false)
                            .addField("Goumen Admiral...", "You shouldn't be receiving this... unless you are doing something wrong", false)
                            .setFooter("Command Name: " + command.getTitle(), handler.me.getAvatarUrl() != null ? handler.me.getAvatarUrl() : handler.me.getDefaultAvatarUrl())
                            .build();
                    handler.channel.sendMessage(embed).queue();
                    suzuya.errorTrace(error.getStackTrace());
                }
            } else {
                String tag = suzuya.tagsHandler.getTagContent(handler.guild.getId(), query);
                if (tag == null) return;
                handler.channel.sendMessage(tag).queue();
            }
        }
    }
}
