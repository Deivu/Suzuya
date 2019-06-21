package suzuya.commands;

import net.dv8tion.jda.core.Permission;
import org.apache.commons.lang3.StringUtils;
import suzuya.structures.BaseCommand;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Settings;

import java.util.Arrays;

public class Tag extends BaseCommand {

    @Override
    public String getTitle() {
        return "tag";
    }

    @Override
    public String getUsage() {
        return "tag <tag_title> <tag_content>";
    }

    @Override
    public String getDescription() {
        return "Adds a new Tag or command or edits the existing ones in your guild. Title will always be lowercase";
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
        if (args.length <= 2)
            return "Admiral, now you forgot to specify the Tag contents. Sighs...";
        String title = args[1].toLowerCase();
        if (title.length() > 20)
            return "Admiral, don't specify more than 20 characters on your Tag title.";
        String content = StringUtils.join(Arrays.copyOfRange(args, 2, args.length), " ");
        if (content.length() > 1024)
            return "Admiral, I humbly ask you to keep your Tag message below 1024 characters. *grins*";
        suzuya.structures.Tag tag = handler.suzuya.tagsHandler.getTag(handler.guild.getId(), title);
        if (tag == null) {
            Boolean results = handler.suzuya.tagsHandler.setTag(handler.author.getId(), handler.guild.getId(), title, content);
            return results ? "Admiral, I saved the Tag " + title + " to my database. You can now use it like a command." : "I failed to edit the Tag, probably there is some issues";
        }
        if (!tag.authorID.equals(handler.author.getId()))
            return "Admiral, there is already a Tag with this title, and you didn't create it. Too bad for you.";
        Boolean results = handler.suzuya.tagsHandler.setTag(handler.author.getId(), handler.guild.getId(), title, content);
        return results ? "Admiral, I edited the your existing Tag without errors." : "I failed to edit the Tag, probably there is some issues";
    }
}

