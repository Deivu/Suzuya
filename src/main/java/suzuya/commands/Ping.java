package suzuya.commands;

import net.dv8tion.jda.core.entities.*;
import suzuya.SuzuyaClient;
import suzuya.handler.CommandHandler;
import suzuya.structures.BaseCommand;

public class Ping extends BaseCommand {

    @Override
    public String getTitle() {
        return "ping";
    }

    @Override
    public String getUsage() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "A simple ping command made harder by Java.";
    }

    @Override
    public String getCategory() {
        return "General";
    }

    @Override
    public void run(SuzuyaClient suzuya, CommandHandler handler, Message msg, Guild guild, User author, Member member, MessageChannel channel, String[] args) {
        channel.sendMessage(String.format("The current ping to Discord API is **%oms**", suzuya.client.getPing())).queue();
    }
}

