package suzuya.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import suzuya.structures.BaseCommand;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Settings;

import java.time.Instant;

public class Status extends BaseCommand {

    @Override
    public String getTitle() {
        return "status";
    }

    @Override
    public String getUsage() {
        return "status";
    }

    @Override
    public String getDescription() {
        return "Returns the current metrics/status of the bot";
    }

    @Override
    public String getCategory() {
        return "General";
    }

    @Override
    public String run(HandlerArgs handler, Settings config, String[] args) {
        double free = Math.round((double) handler.suzuya.system.getFreePhysicalMemorySize() / 1073741824);
        double total = Math.round((double) handler.suzuya.system.getTotalPhysicalMemorySize() / 1073741824);
        double cpuusage = Math.round(handler.suzuya.system.getSystemCpuLoad() * 100);
        double maxalloc = Math.round((double) handler.suzuya.runtime.maxMemory() / 1073741824);
        double allocated = Math.round((double) handler.suzuya.runtime.totalMemory() / 1073741824);
        boolean useMB = false;
        if (allocated < 1.0) {
            allocated = Math.round((double) handler.suzuya.runtime.totalMemory() / 1048576);
            useMB = true;
        }
        MessageEmbed embed = new EmbedBuilder()
                .setColor(handler.suzuya.defaultEmbedColor)
                .setThumbnail(handler.me.getAvatarUrl() != null ? handler.me.getAvatarUrl() : handler.me.getDefaultAvatarUrl())
                .addField(
                        "Program Stats",
                        "```asciidoc\n" +
                                "Guild(s) Count   :: " + handler.suzuya.client.getGuildCache().size() + "\n" +
                                "User(s)  Count   :: " + handler.suzuya.client.getUserCache().size() + "\n" +
                                "Allocated Memory :: " + allocated + (useMB ? " MB" : " GB") + "\n" +
                                "Max Allocated    :: " + maxalloc + " GB" +
                                "```",
                        false
                )
                .addField(
                        "Container Stats",
                        "```asciidoc\n" +
                                "CPU Usage        :: " + cpuusage + " %" + "\n" +
                                "Free Memory      :: " + free + " GB" + "\n" +
                                "Total Memory     :: " + total + " GB" +
                                "```",
                        false
                )
                .addField("Suzuya", "Something placeholder here for now", false)
                .setTimestamp(Instant.now())
                .setFooter("Current Status", handler.me.getAvatarUrl() != null ? handler.me.getAvatarUrl() : handler.me.getDefaultAvatarUrl())
                .build();
        handler.channel.sendMessage(embed).queue();
        return null;
    }
}
