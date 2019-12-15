package suzuya.structures;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.ScheduledFuture;

public class CaptchaExecutor {
    public TextChannel verificationChannel = null;
    public Message message = null;
    public String guildID = null;
    public String userID = null;
    public String text = null;
    public Runnable runnable = null;
    public ScheduledFuture future = null;
}
