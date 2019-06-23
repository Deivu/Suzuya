package suzuya.structures;

import java.util.concurrent.ScheduledFuture;

public class CaptchaExecutor {
    public String guildID = null;
    public String text = null;
    public Runnable runnable = null;
    public ScheduledFuture future = null;
}
