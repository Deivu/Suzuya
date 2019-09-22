package suzuya.util;

import org.json.*;
import suzuya.Sortie;
import suzuya.client.SuzuyaClient;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;

public class Config {
    private final SuzuyaClient suzuya;
    private String token;
    private String prefix;

    public Config(SuzuyaClient suzuya) {
        this.suzuya = suzuya;
        init();
    }

    public String getToken() { return token; }
    public String getPrefix() {
        return prefix;
    }
    public String getDir() {
        String dir = null;
        try {
            File file = new File(Sortie.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            String fileName = file.getName();
            dir = file.getPath().replace(fileName, "");
        } catch (Exception error) {
            error.printStackTrace();
        }
        return dir;
    }

    private void init() {
        try {
            suzuya.SuzuyaLog.info("Working Directory: " + this.getDir());
            suzuya.SuzuyaLog.info(
"                                                \n" +
" ,---.                                          \n" +
"'   .-' ,--.,--.,-----.,--.,--.,--. ,--.,--,--. \n" +
"`.  `-. |  ||  |`-.  / |  ||  | \\  '  /' ,-.  | \n" +
".-'    |'  ''  ' /  `-.'  ''  '  \\   ' \\ '-'  | \n" +
"`-----'  `----' `-----' `----' .-'  /   `--`--' \n" +
"                               `---'            \n"
            );
            InputStream is = new FileInputStream(this.getDir() + "config.json");
            //noinspection ConstantConditions
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
            suzuya.SuzuyaLog.info("Configuration Loaded.");
        } catch (Exception error) {
            suzuya.errorTrace(error.getMessage(), error.getStackTrace());
            System.exit(0);
        }
    }
}
