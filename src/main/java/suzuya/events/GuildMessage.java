package suzuya.events;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import suzuya.client.SuzuyaClient;
import suzuya.structures.BaseCommand;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Settings;

public class GuildMessage extends ListenerAdapter {
    private final SuzuyaClient suzuya;

    public GuildMessage(SuzuyaClient suzuya) {
        this.suzuya = suzuya;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (!suzuya.isClientReady || event.isWebhookMessage()) return;
        HandlerArgs handler = new HandlerArgs(suzuya, event);
        if (handler.author.isBot()) return;
        Settings config = suzuya.settingsHandler.getSettings(handler.guild.getId());
        if (config == null) {
            suzuya.settingsHandler.setDefaults(handler.guild.getId());
            config = suzuya.settingsHandler.getSettings(handler.guild.getId());
            if (config == null) return;
        }
        String content = handler.msg.getContentRaw();
        if (!content.startsWith(config.prefix)) return;
        String[] args = content.split("\\s+");
        String query = args[0].substring(config.prefix.length());
        BaseCommand command = suzuya.commandHandler.getCommand(query);
        if (command == null) {
            String tag = suzuya.tagsHandler.getTagContent(handler.guild.getId(), query);
            if (tag == null) return;
            handler.channel.sendMessage(tag).queue();
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
                handler.channel.sendMessage("Admiral, you don't have the permission **" + missing + "** to use this command.").queue();
            }
        }
        String response = command.run(handler, config, args);
        if (response != null) handler.channel.sendMessage(response).queue();
    }
}
