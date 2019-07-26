package suzuya.config;

import com.google.gson.Gson;

import java.nio.file.*;
import java.io.FileReader;
import java.io.FileNotFoundException;

import suzuya.GeneralUtil;
import suzuya.SuzuyaClient;

public class ConfigManager {
    private final SuzuyaClient suzuya;
    public Config config;

    public ConfigManager(SuzuyaClient suzuya) {
        this.suzuya = suzuya;
        init();
    }

    private void init() {
        try {
            Path configpath = Paths.get(GeneralUtil.pathJoin("config.json"));
            
            if (Files.notExists(configpath)) 
                throw new FileNotFoundException("Cannot find config.json in cwd");
            
            FileReader configfile = new FileReader(configpath.toString());
            
            Gson gson = new Gson();
            this.config = gson.fromJson(configfile, Config.class);
            
            if (this.config.token == null)
                throw new Error("Config Token is null, cannot boot the bot.");
            if (this.config.prefix == null)
                throw new Error("Config Default Prefix is null, cannot boot the bot.");
            
            suzuya.SuzuyaLog.info("Configuration Loaded.");
        } catch (Exception error) {
            suzuya.errorTrace(error.getMessage(), error.getStackTrace());
            System.exit(0);
        }
    }
}
