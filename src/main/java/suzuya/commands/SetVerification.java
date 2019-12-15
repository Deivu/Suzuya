package suzuya.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import suzuya.structures.BaseCommand;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Settings;
import net.dv8tion.jda.api.Permission;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetVerification extends BaseCommand {
    private final Pattern channelPattern = Pattern.compile("<#(\\d{1,20})>");

    @Override
    public String getTitle() {
        return "setverification";
    }

    @Override
    public String getUsage() {
        return "setverification <channel_mention>";
    }

    @Override
    public String getDescription() {
        return "Sets the verification channel on your server.";
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
            if (config.verification_channel == null)
                return "Admiral, verification channel is not configured =3=";
            TextChannel channel = handler.guild.getTextChannelCache().getElementById(config.verification_channel);
            if (channel == null)
                return "Admiral, How I am supposed to send a message in a deleted channel =3=";
            return "Admiral, the verification channel is currently set to **" + channel.getName() + "**";
        }
        Matcher match = channelPattern.matcher(args[1]);
        if (!match.find())
            return "Admiral, you did not even mention a channel =3=";
        TextChannel channel = handler.guild.getTextChannelCache().getElementById(match.group(1));
        if (channel == null)
            return "Admiral, you did not mention a valid channel =3=";
        handler.suzuya.settingsHandler.setDataString(handler.guild.getId(), "verification_channel", channel.getId());
        return "Admiral, the verification channel is now set to **" + channel.getName() + "**";
    }
}