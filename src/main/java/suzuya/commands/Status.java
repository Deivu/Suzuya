package suzuya.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageEmbed;
import suzuya.structures.BaseCommand;
import suzuya.structures.HandlerArgs;
import suzuya.structures.Settings;
import suzuya.TimeUtil;

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
    public boolean ownerOnly() { return false; }

    @Override
    public Permission[] getPermissions() {
        return null;
    }

    @Override
    public String run(HandlerArgs handler, Settings config, String[] args) {
        int allocated_cpu = handler.suzuya.runtime.availableProcessors();
        double totalMemory = handler.suzuya.runtime.totalMemory();
        double freeMemory = handler.suzuya.runtime.freeMemory();
        double systemTotal = handler.suzuya.system.getTotalPhysicalMemorySize();
        double cpu_usage = Math.round(handler.suzuya.system.getSystemCpuLoad() * 100);
        String system_used = convert(systemTotal - handler.suzuya.system.getFreePhysicalMemorySize());
        String system_total = convert(systemTotal);
        String runtime_max_alloc = convert((double) handler.suzuya.runtime.maxMemory());
        String runtime_alloc = convert(totalMemory);
        String runtime_free = convert(freeMemory);
        String used_memory = convert(totalMemory - freeMemory);
        String uptime = TimeUtil.getDurationBreakdown(handler.suzuya.runtime_mx.getUptime(), false);
        MessageEmbed embed = new EmbedBuilder()
                .setColor(handler.suzuya.defaultEmbedColor)
                .setThumbnail(handler.me.getAvatarUrl() != null ? handler.me.getAvatarUrl() : handler.me.getDefaultAvatarUrl())
                .setTitle("• Program Metrics")
                .setDescription(
                                "```asciidoc\n= Program Statistics = \n" +
                                "Guild(s)            :: " + handler.suzuya.client.getGuildCache().size() + "\n" +
                                "Users(s)            :: " + handler.suzuya.client.getUserCache().size() + "\n" +
                                "Text Channel(s)     :: " + handler.suzuya.client.getTextChannelCache().size() + "\n" +
                                "Voice Channel(s)    :: " + handler.suzuya.client.getVoiceChannelCache().size() + "\n" +
                                "Private Channel(s)  :: " + handler.suzuya.client.getPrivateChannelCache().size() + "\n" +
                                "Player(s) Active    :: " + handler.suzuya.players.size() + "\n" +
                                "Allocated Threads   :: " + allocated_cpu + "\n" +
                                "= Memory Statistics = \n" +
                                "Currently Used      :: " + used_memory + "\n" +
                                "Allocated Free      :: " + runtime_free + "\n" +
                                "Allocated Reserved  :: " + runtime_alloc + "\n" +
                                "Maximum Allocatable :: " + runtime_max_alloc + "\n" +
                                "= System Statistics = \n" +
                                "CPU Usage           :: " + cpu_usage + " %" + "\n" +
                                "Memory Usage        :: " + system_used + " / " + system_total + "\n" +
                                "```"
                )
                .addField("• Suzuya || The Shipgirl Project", "Made possible by [JDA](https://github.com/DV8FromTheWorld/JDA) and [Lavaplayer](https://github.com/sedmelluq/lavaplayer). Developed with \\❤ by [Saya](https://github.com/Deivu)", false)
                .setTimestamp(Instant.now())
                .setFooter("Online for: " + uptime, handler.me.getAvatarUrl() != null ? handler.me.getAvatarUrl() : handler.me.getDefaultAvatarUrl())
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
