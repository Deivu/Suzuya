package suzuya.structures;

import net.dv8tion.jda.core.Permission;

public abstract class BaseCommand {

    public abstract String getTitle();

    public abstract String getUsage();

    public abstract String getDescription();

    public abstract String getCategory();

    public abstract boolean ownerOnly();

    public abstract Permission[] getPermissions();

    public abstract String run(HandlerArgs handler, Settings config, String[] args);
}
