package suzuya;

import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import suzuya.handler.CommandHandler;
import suzuya.handler.SettingsHandler;
import suzuya.handler.TagsHandler;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.lang.management.ManagementFactory;

public class SuzuyaClient {
    private final Config config = new Config();

    public final JDA client = new JDABuilder(config.getToken()).build();

    public final CommandHandler commandHandler = new CommandHandler();
    public final SettingsHandler settingsHandler = new SettingsHandler(config);
    public final TagsHandler tagsHandler = new TagsHandler(config);

    public final Color defaultEmbedColor = new Color(62, 180, 137);

    public final OperatingSystemMXBean system = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    public final Runtime runtime = Runtime.getRuntime();

    public Boolean isClientReady = false;

    SuzuyaClient() throws LoginException {
        System.out.println("Working Directory is in:" + config.getDir());
        settingsHandler.initDb("Suzuya.db");
        tagsHandler.initDb("SuzuyaTags.db");
    }
}
