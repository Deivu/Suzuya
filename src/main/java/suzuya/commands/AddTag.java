package suzuya.commands;

import suzuya.SuzuyaClient;
import suzuya.structures.BaseCommand;

import java.util.Arrays;

import net.dv8tion.jda.core.entities.*;
import org.apache.commons.lang3.StringUtils;
import suzuya.structures.Tag;

public class AddTag extends BaseCommand {

    public String getTitle() {
        return "addtag";
    }

    public String getUsage() {
        return "addtag <tag_title>";
    }

    public String getDescription() {
        return "Adds a new tag or command or edits the existing ones in your guild. Title will always be lowercase";
    }

    public String getCategory() {
        return "Tags";
    }

    public String run(SuzuyaClient suzuya, Message msg, Guild guild, User author, Member member, MessageChannel channel, String[] args) {
        if (args[1] == null)
            return "Admiral, you forgot to specify the tag title, dummy.";
        if (args[2] == null)
            return "Admiral, now you forgot to specify the tag contents. Sighs...";
        String title = args[1].toLowerCase();
        if (title.length() > 20)
            return "Admiral, don't specify more than 20 characters on your tag title.";
        String content = StringUtils.join(Arrays.copyOfRange(args, 2, args.length), " ");
        if (content.length() > 1024)
            return "Admiral, I humbly ask you to keep your tag message below 1024 characters. *grins*";
        Tag tag = suzuya.tagsHandler.getTag(guild.getId(), title);
        if (tag == null) {
            Boolean results = suzuya.tagsHandler.setTag(author.getId(), guild.getId(), title, content);
            return results ? "Admiral, I saved the tag " + title + " to my database. You can now use it like a command." : "I failed to edit the tag, probably there is some issues";
        }
        if (!tag.authorID.equals(author.getId()))
            return "Admiral, there is already a tag with this title, and you didn't create it. Too bad for you.";
        Boolean results = suzuya.tagsHandler.setTag(author.getId(), guild.getId(), title, content);
        return results ? "Admiral, I edited the your existing tag without errors." : "I failed to edit the tag, probably there is some issues";
    }
}

