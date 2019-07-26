/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package suzuya;

import java.nio.file.Paths;

/**
 *
 * @author User
 */
public class GeneralUtil {
    public static String pathJoin(String... paths) {
        String cwd = System.getProperty("user.dir");
        for (String path: paths) {
            cwd = Paths.get(cwd, path).toString();
        }
        return cwd;
    }
}
