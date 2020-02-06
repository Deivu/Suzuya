package suzuya.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import suzuya.structures.BaseCommand;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Settings;

import java.util.List;

public class EmojiLink extends BaseCommand {
    @Override
    public String getTitle() {
        return "emojilink";
    }

    @Override
    public String getUsage() {
        return "emojilink <emoji>";
    }

    @Override
    public String getDescription() {
        return "Shows a link to a specific emoji.";
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
        List<Emote> emotes = handler.msg.getEmotes();
        if (emotes.isEmpty()) return "Please input an emoji to get the link on";
        Emote emote = emotes.get(0);
        return emote.getImageUrl();
    }
}
