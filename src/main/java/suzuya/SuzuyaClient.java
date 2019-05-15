package suzuya;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import suzuya.handler.SettingsHandler;
import suzuya.handler.TagsHandler;

import javax.security.auth.login.LoginException;
import java.awt.*;

public class SuzuyaClient {
    public Config config;
    public JDA client;
    public SettingsHandler settingsHandler;
    public TagsHandler tagsHandler;

    public Color defaultEmbedColor = new Color(62, 180, 137);
    public Boolean isClientReady = false;


    public SuzuyaClient() throws LoginException {
        config = new Config();
        System.out.println("Working Directory is in:" + config.getDir());
        client = new JDABuilder(config.getToken()).build();
        settingsHandler = new SettingsHandler(config);
        settingsHandler.initDb("Suzuya.db");
        tagsHandler = new TagsHandler(config);
        tagsHandler.initDb("SuzuyaTags.db");
    }

}
