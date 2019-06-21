package suzuya.commands;

import net.dv8tion.jda.core.Permission;
import suzuya.structures.BaseCommand;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Settings;


public class Ping extends BaseCommand {

    @Override
    public String getTitle() {
        return "ping";
    }

    @Override
    public String getUsage() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "A simple ping command made harder by Java.";
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
        return String.format("The current ping to Discord API is **%oms**", handler.suzuya.client.getPing());
    }
}

