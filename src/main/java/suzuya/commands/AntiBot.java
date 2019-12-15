package suzuya.commands;

import suzuya.structures.BaseCommand;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Settings;
import net.dv8tion.jda.api.Permission;

public class AntiBot extends BaseCommand {

    @Override
    public String getTitle() {
        return "antibot";
    }

    @Override
    public String getUsage() {
        return "antibot <true or false>";
    }

    @Override
    public String getDescription() {
        return "Enables / Disables the Anti User Lewd Bot.";
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
        if (args.length <= 1)
            return "Admiral, the Anti Bot feature is currently **" + config.auto_ban + "**";
        if (!(args[1].equals("true") || args[1].equals("false")))
            return "Admiral, please keep your answer to **true** or **false** case-sensitive please =.=";
        handler.suzuya.settingsHandler.setDataString(handler.guild.getId(), "auto_ban", args[1]);
        return "Admiral, the Anti Bot feature is now set to **" + args[1] + "**";
    }
}