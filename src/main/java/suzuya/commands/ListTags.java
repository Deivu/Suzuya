package suzuya.commands;

import suzuya.structures.BaseCommand;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Settings;


public class ListTags extends BaseCommand {
    @Override
    public String getTitle() {
        return "listtags";
    }

    @Override
    public String getUsage() {
        return "listtags <tag_title>";
    }

    @Override
    public String getDescription() {
        return "Lists all the available tags to use.";
    }

    @Override
    public String getCategory() {
        return "Tags";
    }

    @Override
    public String run(HandlerArgs handler, Settings config, String[] args) {
        return null;
    }
}
