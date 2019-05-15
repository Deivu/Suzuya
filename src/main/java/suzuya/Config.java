package suzuya;

import org.json.*;

import java.io.InputStream;
import java.io.FileInputStream;

public class Config {

    private String token;

    private String prefix;

    public Config() {
        init();
    }

    public String getToken() {
        return token;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getDir() {
        return Suzuya.class.getProtectionDomain().getCodeSource().getLocation().getFile();
    }

    private void init() {
        try {
            InputStream is = new FileInputStream(this.getDir() + "config.json");
            if (is == null) {
                throw new NullPointerException("Cannot find config.json");
            }
            JSONTokener tokenized = new JSONTokener(is);
            JSONObject config = new JSONObject(tokenized);
            this.token = config.getString("token");
            this.prefix = config.getString("default_prefix");
            if (this.token == null)
                throw new Error("Config Token is null, cannot boot the bot.");
            if (this.prefix == null)
                throw new Error("Config Default Prefix is null, cannot boot the bot.");
            System.out.println("Loaded Configuration");
        } catch (Exception error) {
            error.printStackTrace();
            System.exit(0);
        }
    }
}
