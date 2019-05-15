package suzuya.structures;

import net.dv8tion.jda.core.entities.*;
import suzuya.SuzuyaClient;

public abstract class BaseCommand {

    public abstract String getTitle();

    public abstract String getUsage();

    public abstract String getDescription();

    public abstract String getCategory();

    public abstract void run(SuzuyaClient suzuya, Message msg, Guild guild, User author, Member member, MessageChannel channel, String[] args);
}
