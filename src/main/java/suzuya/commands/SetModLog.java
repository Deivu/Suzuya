package suzuya.commands;

import net.dv8tion.jda.core.entities.TextChannel;
import suzuya.structures.BaseCommand;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Settings;
import net.dv8tion.jda.core.Permission;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetModLog extends BaseCommand {
    private Pattern channelPattern = Pattern.compile("<#(\\d{1,20})>");

    @Override
    public String getTitle() {
        return "setmodlog";
    }

    @Override
    public String getUsage() {
        return "setmodlog <new_prefix>";
    }

    @Override
    public String getDescription() {
        return "Changes the mod-log channel to the one you want";
    }

    @Override
    public String getCategory() {
        return "Config";
    }

    @Override
    public boolean ownerOnly() { return false; }

    @Override
    public Permission[] getPermissions() {
        return new Permission[]{ Permission.MANAGE_SERVER };
    }

    @Override
    public String run(HandlerArgs handler, Settings config, String[] args) {
        if (args.length <= 1) {
            if (config.mod_log == null)
                return "Admiral, mod-log is not configured =3=";
            TextChannel channel = handler.guild.getTextChannelCache().getElementById(config.mod_log);
            if (channel == null)
                return "Admiral, How I am supposed to send a message in a deleted channel =3=";
            return "Admiral, the Mod-Logs is currently set to **" + channel.getName() + "**";
        }
        Matcher match = channelPattern.matcher(args[1]);
        if (!match.find())
            return "Admiral, you did not even mention a channel =3=";
        TextChannel channel = handler.guild.getTextChannelCache().getElementById(match.group(1));
        if (channel == null)
            return "Admiral, you did not mention a valid channel =3=";
        handler.suzuya.settingsHandler.setDataString(handler.guild.getId(), "mod_log", channel.getId());
        return "Admiral, the mod-log channel is now set to **" + channel.getName() + "**";
    }
}