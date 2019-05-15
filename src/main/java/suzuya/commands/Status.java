package suzuya.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import suzuya.SuzuyaClient;
import suzuya.handler.CommandHandler;
import suzuya.structures.BaseCommand;

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
    public void run(SuzuyaClient suzuya, Message msg, Guild guild, User author, Member member, MessageChannel channel, String[] args) {
        SelfUser me = suzuya.client.getSelfUser();
        // will clean it in future probably.
        double free = Math.round((double)suzuya.system.getFreePhysicalMemorySize() / 1073741824);
        double total = Math.round((double)suzuya.system.getTotalPhysicalMemorySize() / 1073741824);
        double cpuusage = Math.round(suzuya.system.getSystemCpuLoad() * 100);
        double maxalloc = Math.round((double)suzuya.runtime.maxMemory() / 1073741824);
        double allocated = Math.round((double)suzuya.runtime.totalMemory() / 1073741824);
        boolean useMB = false;
        if (allocated < 1.0) {
            allocated = Math.round((double) suzuya.runtime.totalMemory() / 1048576);
            useMB = true;
        }
        MessageEmbed embed = new EmbedBuilder()
                .setColor(suzuya.defaultEmbedColor)
                .setThumbnail(me.getAvatarUrl() != null ? me.getAvatarUrl() : me.getDefaultAvatarUrl())
                .addField("Program Stats", "```asciidoc\n" +
                        "Guild(s) Count   :: " + suzuya.client.getGuildCache().size() + "\n" +
                        "User(s)  Count   :: " + suzuya.client.getUserCache().size() + "\n" +
                        "Allocated Memory :: " + allocated + (useMB ? " MB" : " GB") + "\n" +
                        "Max Allocated    :: " + maxalloc + " GB" + "```", false)
                .addField("Container Stats", "```asciidoc\n" +
                        "CPU Usage        :: " + cpuusage + " %" + "\n" +
                        "Free Memory      :: " + free + " GB" + "\n" +
                        "Total Memory     :: " + total + " GB"
                        + "```", false)
                .addField("Suzuya", "Something placeholder here for now", false)
                .setTimestamp(Instant.now())
                .setFooter("Current Status", me.getAvatarUrl() != null ? me.getAvatarUrl() : me.getDefaultAvatarUrl())
                .build();
        channel.sendMessage(embed).queue();
    }
}
