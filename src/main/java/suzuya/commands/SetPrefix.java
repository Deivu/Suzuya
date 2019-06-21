package suzuya.commands;

import suzuya.structures.BaseCommand;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Settings;
import net.dv8tion.jda.core.Permission;

public class SetPrefix extends BaseCommand {

    @Override
    public String getTitle() {
        return "setprefix";
    }

    @Override
    public String getUsage() {
        return "setprefix <new_prefix>";
    }

    @Override
    public String getDescription() {
        return "Changes the guild prefix to the one you want";
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
            return "Admiral, what you will do without me... The prefix for this guild is **" + config.prefix + "**. Please don't forget it next time.";
        if (args[1].length() > 4)
            return "Admiral, please keep the prefix below 4 characters.";
        handler.suzuya.settingsHandler.setDataString(handler.guild.getId(), "prefix", args[1]);
        return "Admiral, Please don't forget your prefix is now **" + args[1] + "**";
    }
}