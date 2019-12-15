package suzuya.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import suzuya.structures.BaseCommand;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Settings;

public class SetSilenced extends BaseCommand {
    @Override
    public String getTitle() {
        return "setsilenced";
    }

    @Override
    public String getUsage() {
        return "setsilenced <role_id>";
    }

    @Override
    public String getDescription() {
        return "Sets the silenced role";
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
            Role role = handler.guild.getRoleById(config.silenced_role);
            if (role == null)
                return "Admiral, silenced role is not yet set";
            return "Admiral, the Anti Bot feature is currently **" + config.auto_ban + "**";
        }

        Role role = handler.guild.getRoleById(args[1]);
        if (role == null)
            return "Admiral, I cannot find this role. Try again.";

        handler.suzuya.settingsHandler.setDataString(handler.guild.getId(), "silenced_role", role.getId());

        return "Admiral, the Silenced Role is now configured to **" + role.getName() + "**";
    }
}
