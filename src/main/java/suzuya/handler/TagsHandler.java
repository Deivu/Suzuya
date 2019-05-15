package suzuya.handler;

import suzuya.Config;
import java.sql.*;

public class TagsHandler {
    Config config;
    Connection connection;

    public TagsHandler(Config settings) { config = settings; }

    public void initDb(String db)
    {
        String location = "jdbc:sqlite:" + config.getDir() + db;
        try {
            connection = DriverManager.getConnection(location);
            if (connection != null) System.out.println("Connected to Tags Database.");
        } catch (Exception error) {
            error.printStackTrace();
        } finally {
            if (connection == null) {
                System.out.println("No connection to Tags Database. Exiting");
                System.exit(0);
            }
            try {
                String sql = "CREATE TABLE IF NOT EXISTS tags(" +
                        "user_id TEXT PRIMARY KEY," +
                        "guild_id TEXT NOT NULL," +
                        "title TEXT NOT NULL," +
                        "content TEXT NOT NULL," +
                        "timestamp INTEGER NOT NULL" +
                        ")";
                PreparedStatement cmd = connection.prepareStatement(sql);
                cmd.execute();
            } catch (Exception error) {
                error.printStackTrace();
                System.exit(0);
            }
        }
    }
}
