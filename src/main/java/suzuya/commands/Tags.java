package suzuya.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.apache.commons.lang3.StringUtils;
import suzuya.structures.*;

import java.util.ArrayList;
import java.util.List;


public class Tags extends BaseCommand {

    @Override
    public String getTitle() {
        return "tags";
    }

    @Override
    public String getUsage() {
        return "tags <page>";
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
        ArrayList<Tag> tags = handler.suzuya.tagsHandler.listGuildTags(handler.guild.getId());
        if (tags == null)
            return "Admiral, there is no available tag/commands in this guild.";
        int request;
        try {
            request = Integer.parseInt(args[1]);
            if (request < 0) request = 1;
        } catch (Exception error) {
            request = 1;
        }
        Page data = handler.suzuya.paginate(tags.size(), request, 20);
        List<Tag> parts = tags.subList(data.start, data.end);
        ArrayList<String> titles = new ArrayList<>();
        for (Tag part : parts) {
            titles.add(String.format("`%s`", part.title));
        }
        MessageEmbed embed = new EmbedBuilder()
                .setColor(handler.suzuya.defaultEmbedColor)
                .setThumbnail(handler.me.getAvatarUrl() != null ? handler.me.getAvatarUrl() : handler.me.getDefaultAvatarUrl())
                .setTitle("Available tags/commands in this guild.")
                .setDescription(StringUtils.join(titles, ", "))
                .setFooter(data.current + " / " + data.max + " page(s) | " + tags.size() + " available tags", handler.me.getAvatarUrl() != null ? handler.me.getAvatarUrl() : handler.me.getDefaultAvatarUrl())
                .build();
        handler.channel.sendMessage(embed).queue();
        return null;
    }
}
