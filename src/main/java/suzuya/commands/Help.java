package suzuya.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.apache.commons.lang3.StringUtils;
import suzuya.structures.BaseCommand;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Settings;

import java.time.Instant;

public class Help extends BaseCommand {

    @Override
    public String getTitle() {
        return "help";
    }

    @Override
    public String getUsage() {
        return "help <command>";
    }

    @Override
    public String getDescription() {
        return "Shows the help menu, or if supplied with args, gets the description of that command.";
    }

    @Override
    public String getCategory() {
        return "General";
    }

    @Override
    public boolean ownerOnly() { return false; }

    @Override
    public Permission[] getPermissions() {
        return null;
    }

    @Override
    public String run(HandlerArgs handler, Settings config, String[] args) {
        if (args.length == 2) {
            BaseCommand command = handler.suzuya.commandHandler.getCommand(args[1]);
            if (command == null) return null;
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(handler.suzuya.defaultEmbedColor)
                    .setThumbnail(handler.me.getAvatarUrl() != null ? handler.me.getAvatarUrl() : handler.me.getDefaultAvatarUrl())
                    .addField("Command Usage", command.getUsage(), false)
                    .addField("Command Description", command.getDescription(), false)
                    .setTimestamp(Instant.now())
                    .build();
            handler.channel.sendMessage(embed).queue();
            return null;
        }
        MessageEmbed embed = new EmbedBuilder()
                .setColor(handler.suzuya.defaultEmbedColor)
                .setTitle(String.format("\\❔ %s's Help Menu", handler.me.getName()))
                .setDescription("Use **" + config.prefix + "help <command>** for more info about a command.")
                .setThumbnail(handler.me.getAvatarUrl() != null ? handler.me.getAvatarUrl() : handler.me.getDefaultAvatarUrl())
                .addField("• General", StringUtils.join(handler.suzuya.commandHandler.getCommandsInCategory("General").toArray(), ", "), false)
                .addField("• Tags", StringUtils.join(handler.suzuya.commandHandler.getCommandsInCategory("Tags").toArray(), ", "), false)
                .addField("• Moosik", StringUtils.join(handler.suzuya.commandHandler.getCommandsInCategory("Moosik").toArray(), ", "), false)
                .addField("• Config", StringUtils.join(handler.suzuya.commandHandler.getCommandsInCategory("Config").toArray(), ", "), false)
                .addField("\\➡ Additional Links", "[Source](https://github.com/Deivu/Suzuya) | [Issues](https://github.com/Deivu/Suzuya/issues) | [Support](https://discordapp.com/invite/FVqbtGu) | [Donate](https://www.patreon.com/user?u=12059417)", false)
                .setFooter("Total Commands: " + handler.suzuya.commandHandler.getCommandsSize(), handler.me.getAvatarUrl() != null ? handler.me.getAvatarUrl() : handler.me.getDefaultAvatarUrl())
                .setTimestamp(Instant.now())
                .build();
        handler.channel.sendMessage(embed).queue();
        return null;
    }
}
