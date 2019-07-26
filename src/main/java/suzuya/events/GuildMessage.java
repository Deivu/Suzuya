package suzuya.events;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
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
        if (config == null) {
            suzuya.settingsHandler.setDefaults(handler.guild.getId());
            config = suzuya.settingsHandler.getSettings(handler.guild.getId());
            // If still null, lets just return
            if (config == null) return;
        }
        String content = handler.msg.getContentRaw();
        if (!content.startsWith(config.prefix)) return;
        String[] args = content.split("\\s+");
        String query = args[0].substring(config.prefix.length()).toLowerCase();
        BaseCommand command = suzuya.commandHandler.getCommand(query);
        if (command == null) {
            try {
                String tag = suzuya.tagsHandler.getTagContent(handler.guild.getId(), query);
                if (tag == null) return;
                handler.channel.sendMessage(tag).queue();
            } catch (Exception error) {
                HandleError(error, handler);
            }
            return;
        }
        Permission[] perms = command.getPermissions();
        if (perms != null) {
            String missing = null;
            for (Permission perm : perms) {
                if (!handler.member.hasPermission(perm)) {
                    missing = perm.getName();
                    break;
                }
            }
            if (missing != null) {
                try {
                    handler.channel.sendMessage("Admiral, you don't have the permission **" + missing + "** to use this command.").queue();
                } catch (Exception error) {
                    HandleError(error, handler);
                }
            }
        }
        try {
            String response = command.run(handler, config, args);
            if (response != null) handler.channel.sendMessage(response).queue();
        } catch (Exception error) {
            HandleError(error, handler, command);
        }
    }

    private void HandleError(Exception error, HandlerArgs handler, BaseCommand command) {
        String commandName = command == null ? "Tags System" : command.getTitle();
        MessageEmbed embed = new EmbedBuilder()
                .setColor(suzuya.defaultEmbedColor)
                .setTitle("• Command Error")
                .setDescription("```java\n" + error.toString() + "```")
                .addField("Goumen Admiral...", "You shouldn't be receiving this... unless you are doing something wrong", false)
                .setFooter("Module Name: " + commandName, handler.me.getAvatarUrl() != null ? handler.me.getAvatarUrl() : handler.me.getDefaultAvatarUrl())
                .build();
        try {
            handler.channel.sendMessage(embed).queue();
        } catch (Exception _error) {
            suzuya.errorTrace(_error.getMessage(), _error.getStackTrace());
        }
        suzuya.errorTrace(error.getMessage(), error.getStackTrace());
    }

    private void HandleError(Exception error, HandlerArgs handler) {
        MessageEmbed embed = new EmbedBuilder()
                .setColor(suzuya.defaultEmbedColor)
                .setTitle("• Command Error")
                .setDescription("```java\n" + error.toString() + "```")
                .addField("Goumen Admiral...", "You shouldn't be receiving this... unless you are doing something wrong", false)
                .setFooter("Module Name: Tags System", handler.me.getAvatarUrl() != null ? handler.me.getAvatarUrl() : handler.me.getDefaultAvatarUrl())
                .build();
        try {
            handler.channel.sendMessage(embed).queue();
        } catch (Exception _error) {
            suzuya.errorTrace(_error.getMessage(), _error.getStackTrace());
        }
        handler.channel.sendMessage(embed).queue();
        suzuya.errorTrace(error.getMessage(), error.getStackTrace());
    }
}
