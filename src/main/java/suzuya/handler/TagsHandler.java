package suzuya.handler;

import suzuya.Config;
import suzuya.structures.Tag;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.ArrayList;

public class TagsHandler {
    private final Config config;
    private Connection connection;

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
                        "user_id TEXT NOT NULL," +
                        "guild_id TEXT NOT NULL," +
                        "title TEXT NOT NULL," +
                        "content TEXT NOT NULL," +
                        "timestamp UNSIGNED BIG INT NOT NULL," +
                        "UNIQUE(user_id, guild_id, title)" +
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

    public Tag getTag(String guild_id, String title) {
        Tag tag = new Tag();
        try {
            PreparedStatement cmd = connection.prepareStatement("SELECT * FROM tags WHERE guild_id = ? AND title = ?");
            cmd.setString(1, guild_id);
            cmd.setString(2, title);
            try {
                ResultSet results = cmd.executeQuery();
                //noinspection TryFinallyCanBeTryWithResources
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

    public ArrayList<Tag> listGuildTags(String guild_id) {
        ArrayList<Tag> tags = new ArrayList<>();
        try {
            PreparedStatement cmd = connection.prepareStatement("SELECT * FROM tags WHERE guild_id = ?");
            cmd.setString(1, guild_id);
            try {
                ResultSet results = cmd.executeQuery();
                //noinspection TryFinallyCanBeTryWithResources
                try {
                    while (results.next()) {
                        Tag tag = new Tag();
                        tag.authorID = results.getString("user_id");
                        tag.guildID = results.getString("guild_id");
                        tag.title = results.getString("title");
                        tag.content = results.getString("content");
                        tag.timestamp = results.getInt("timestamp");
                        tag.exists = true;
                        tags.add(tag);
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
        return tags.size() >= 1 ? tags : null;
    }

    public String getTagContent(String guild_id, String title) {
        String content = null;
        try {
            PreparedStatement cmd = connection.prepareStatement("SELECT content FROM tags WHERE guild_id = ? AND title = ?");
            cmd.setString(1, guild_id);
            cmd.setString(2, title);
            try {
                ResultSet results = cmd.executeQuery();
                //noinspection TryFinallyCanBeTryWithResources
                try {
                    if (results.next()) content = results.getString("content");
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
        return content;
    }

    public Boolean setTag(String authorID, String guildID, String title, String content) {
        boolean status = false;
        String sql = "INSERT OR REPLACE INTO tags" +
                "(user_id, guild_id, title, content, timestamp)" +
                "VALUES" +
                "(?, ?, ?, ?, ?)";
        Instant now = Instant.now();
        try {
            PreparedStatement cmd = connection.prepareStatement(sql);
            //noinspection TryFinallyCanBeTryWithResources
            try {
                cmd.setString(1, authorID);
                cmd.setString(2, guildID);
                cmd.setString(3, title);
                cmd.setString(4, content);
                cmd.setLong(5, now.getEpochSecond());
                int update = cmd.executeUpdate();
                if (update > 0) status = true;
            } catch (Exception error) {
                error.printStackTrace();
            } finally {
                cmd.close();
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
        return status;
    }

    public Boolean deleteTag(String guildID, String title) {
        boolean status = false;
        String sql = "DELETE FROM tags WHERE guild_id = ? AND title = ?";
        try {
            PreparedStatement cmd = connection.prepareStatement(sql);
            //noinspection TryFinallyCanBeTryWithResources
            try {
                cmd.setString(1, guildID);
                cmd.setString(2, title);
                int update = cmd.executeUpdate();
                if (update > 0) status = true;
            } catch (Exception error) {
                error.printStackTrace();
            } finally {
                cmd.close();
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
        return status;
    }
}
