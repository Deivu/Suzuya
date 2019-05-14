package suzuya.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import org.apache.commons.lang3.StringUtils;
import suzuya.SuzuyaClient;
import suzuya.handler.CommandHandler;
import suzuya.structures.BaseCommand;

public class Help extends BaseCommand {
    @Override
    public String getTitle()
    {
        return "help";
    }

    @Override
    public String getUsage()
    {
        return "help <command>";
    }

    @Override
    public String getDescription()
    {
        return "Shows the help menu, or if supplied with args, gets the description of that command.";
    }

    @Override
    public String getCategory()
    {
        return "General";
    }

    @Override
    public void run(SuzuyaClient suzuya, CommandHandler handler, Message msg, Guild guild, User author, Member member, MessageChannel channel, String[] args)
    {
        SelfUser me = suzuya.client.getSelfUser();
        if (args.length == 2) {
            BaseCommand command = handler.getCommand(args[1]);
            if (command == null) return;
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(suzuya.defaultEmbedColor)
                    .setThumbnail(me.getAvatarUrl() != null ? me.getAvatarUrl() : me.getDefaultAvatarUrl())
                    .addField("Command Usage", command.getUsage(), false)
                    .addField("Command Description", command.getDescription(), false)
                    .build();
            channel.sendMessage(embed).queue();
            return;
        }

        MessageEmbed embed = new EmbedBuilder()
                .setColor(suzuya.defaultEmbedColor)
                .setThumbnail(me.getAvatarUrl() != null ? me.getAvatarUrl() : me.getDefaultAvatarUrl())
                .addField(String.format("%s's Help", me.getName()), "Use <prefix>help <command> for more info.", false)
                .addField("General", StringUtils.join(handler.getCommandsInCategory("General").toArray(), ", "), false)
                .build();
        channel.sendMessage(embed).queue();
    }
}
