package suzuya;

import org.json.*;
import java.io.InputStream;
import java.io.FileInputStream;

public class Config {

    private String token = "";

    private String prefix = "";

    public String getToken()
    {
        return token;
    }

    public String getPrefix() { return prefix; }

    public String getDir() { return Suzuya.class.getProtectionDomain().getCodeSource().getLocation().getFile(); }

    public void init() {
        try {
            InputStream is = new FileInputStream(this.getDir() + "config.json");
            if (is == null) {
                throw new NullPointerException("Cannot find config.json");
            }
            JSONTokener tokenized = new JSONTokener(is);
            JSONObject config = new JSONObject(tokenized);
            this.token = config.getString("token");
            this.prefix = config.getString("default_prefix");
            System.out.println("Loaded Configuration");
        } catch (Exception error) {
            System.out.println(error);
            System.exit(0);
        }

    }
}
