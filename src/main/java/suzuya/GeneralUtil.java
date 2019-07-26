package suzuya;

import java.nio.file.Paths;

public class GeneralUtil {
    public static String pathJoin(String... paths) {
        String cwd = System.getProperty("user.dir");
        for (String path: paths) {
            cwd = Paths.get(cwd, path).toString();
        }
        return cwd;
    }
}
