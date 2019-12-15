package suzuya.commands;

import suzuya.structures.BaseCommand;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Settings;
import net.dv8tion.jda.api.Permission;

public class DeleteTag extends BaseCommand {

    @Override
    public String getTitle() {
        return "deletetag";
    }

    @Override
    public String getUsage() {
        return "deletetag <tag_title>";
    }

    @Override
    public String getDescription() {
        return "Removes an existing Tag from the database.";
    }

    @Override
    public String getCategory() {
        return "Tags";
    }

    @Override
    public boolean ownerOnly() { return false; }

    @Override
    public Permission[] getPermissions() {
        return null;
    }

    @Override
    public String run(HandlerArgs handler, Settings config, String[] args) {
        if (args.length <= 1)
            return "Admiral, you forgot to specify the Tag title, dummy.";
        String title = args[1].toLowerCase();
        suzuya.structures.Tag tag = handler.suzuya.tagsHandler.getTag(handler.guild.getId(), title);
        if (tag == null)
            return "Baka Admiral, this Tag does not exist, hence I cannot delete it";
        if (!handler.member.getPermissions().contains(Permission.MESSAGE_MANAGE)) {
            if (!tag.authorID.equals(handler.author.getId()))
                return "Only the Tag owner, or a person with Manage Messages can delete tags";
        }
        Boolean deleted = handler.suzuya.tagsHandler.deleteTag(handler.guild.getId(), title);
        return deleted ? "Successfully deleted this Tag" : "Admiral, Seems like I cannot delete this Tag after all";
    }
}
