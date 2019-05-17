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
        double cpuusage = Math.round(handler.suzuya.system.getSystemCpuLoad() * 100);
        String free = convert((double) handler.suzuya.system.getFreePhysicalMemorySize());
        String total = convert((double) handler.suzuya.system.getTotalPhysicalMemorySize());
        String maxalloc = convert((double) handler.suzuya.runtime.maxMemory());
        String allocated = convert((double) handler.suzuya.runtime.totalMemory());
        MessageEmbed embed = new EmbedBuilder()
                .setColor(handler.suzuya.defaultEmbedColor)
                .setThumbnail(handler.me.getAvatarUrl() != null ? handler.me.getAvatarUrl() : handler.me.getDefaultAvatarUrl())
                .addField(
                        "Program Stats",
                        "```asciidoc\n" +
                              "Guild(s) Count   :: " + handler.suzuya.client.getGuildCache().size() + "\n" +
                              "User(s)  Count   :: " + handler.suzuya.client.getUserCache().size() + "\n" +
                              "Allocated Memory :: " + allocated + "\n" +
                              "Max Allocated    :: " + maxalloc +
                              "```",
                        false
                )
                .addField(
                        "Container Stats",
                        "```asciidoc\n" +
                              "CPU Usage        :: " + cpuusage + " %" + "\n" +
                              "Free Memory      :: " + free + "\n" +
                              "Total Memory     :: " + total +
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

    private String convert(Double value) {
        String parsed;
        if (value < 1000000000) {
            double data = Math.round(value / 1048576);
            parsed = String.format("%s MB", data);
            return parsed;
        }
        double data = Math.round(value / 1073741824);
        parsed = String.format("%s GB", data);
        return parsed;
    }
}
