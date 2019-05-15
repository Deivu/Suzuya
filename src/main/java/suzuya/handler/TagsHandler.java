package suzuya.handler;

import suzuya.Config;

import java.sql.*;

import suzuya.structures.Tag;

public class TagsHandler {
    Config config;
    Connection connection;

    public TagsHandler(Config settings) {
        config = settings;
    }

    public void initDb(String db) {
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
                cmd.close();
            } catch (Exception error) {
                error.printStackTrace();
                System.exit(0);
            }
        }
    }

    public Tag getTag(String user_id, String title) {
        Tag tag = new Tag();
        try {
            PreparedStatement cmd = connection.prepareStatement("SELECT * FROM tags WHERE user_id = ? AND title = ?");
            cmd.setString(1, user_id);
            cmd.setString(2, title);
            try {
                ResultSet results = cmd.executeQuery();
                try {
                    if (results.next()) {
                        tag.authorID = results.getString("user_id");
                        tag.guildID = results.getString("guild_id");
                        tag.title = results.getString("title");
                        tag.content = results.getString("content");
                        tag.timestamp = results.getInt("timestamp");
                        tag.exists = true;
                    }
                } catch (Exception error) {
                    error.printStackTrace();
                } finally {
                    results.close();
                }
            } catch (Exception error) {
                error.printStackTrace();
            } finally {
                cmd.close();
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
        return tag.exists ? tag : null;
    }
}
