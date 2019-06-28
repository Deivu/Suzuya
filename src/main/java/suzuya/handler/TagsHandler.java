package suzuya.handler;

import org.h2.jdbcx.JdbcConnectionPool;
import suzuya.SuzuyaClient;
import suzuya.structures.Tag;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;

public class TagsHandler {
    private final SuzuyaClient suzuya;
    private final JdbcConnectionPool pool;

    public TagsHandler(SuzuyaClient suzuya) {
        this.suzuya = suzuya;
        this.pool = JdbcConnectionPool.create(
                "jdbc:h2:file:" + suzuya.config.getDir() + "\\db\\SuzuyaTags;MULTI_THREADED=1",
                "",
                ""
        );
        suzuya.SuzuyaLog.info("Connected to Tags Database.");
    }

    public void initDb() {
        String sql = "CREATE TABLE IF NOT EXISTS tags(" +
                "user_id TEXT NOT NULL," +
                "guild_id TEXT NOT NULL," +
                "title TEXT NOT NULL," +
                "content TEXT NOT NULL," +
                "timestamp UNSIGNED BIG INT NOT NULL," +
                "UNIQUE(user_id, guild_id, title)" +
                ")";
        try (Connection connection = pool.getConnection()) {
            try (PreparedStatement cmd = connection.prepareStatement(sql)) {
                cmd.execute();
            }
        } catch (Exception error) {
            suzuya.errorTrace(error.getMessage(), error.getStackTrace());
            System.exit(0);
        }
    }

    public Tag getTag(String guild_id, String title) {
        String sql = "SELECT * FROM tags WHERE guild_id = ? AND title = ?";
        Tag tag = new Tag();
        try (Connection connection = pool.getConnection()) {
            try (PreparedStatement cmd = connection.prepareStatement(sql)) {
                cmd.setString(1, guild_id);
                cmd.setString(2, title);
                try (ResultSet results = cmd.executeQuery()) {
                    if (results.next()) {
                        tag.authorID = results.getString("user_id");
                        tag.guildID = results.getString("guild_id");
                        tag.title = results.getString("title");
                        tag.content = results.getString("content");
                        tag.timestamp = results.getInt("timestamp");
                        tag.exists = true;
                    }
                }
            }
        } catch (Exception error) {
            suzuya.errorTrace(error.getMessage(), error.getStackTrace());
        }
        return tag.exists ? tag : null;
    }

    public ArrayList<Tag> listGuildTags(String guild_id) {
        String sql = "SELECT * FROM tags WHERE guild_id = ?";
        ArrayList<Tag> tags = new ArrayList<>();
        try (Connection connection = pool.getConnection()) {
            try (PreparedStatement cmd = connection.prepareStatement(sql)) {
                cmd.setString(1, guild_id);
                try (ResultSet results = cmd.executeQuery()) {
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
                }
            }
        } catch (Exception error) {
            suzuya.errorTrace(error.getMessage(), error.getStackTrace());
        }
        return tags.size() >= 1 ? tags : null;
    }

    public String getTagContent(String guild_id, String title) {
        String sql = "SELECT content FROM tags WHERE guild_id = ? AND title = ?";
        String content = null;
        try (Connection connection = pool.getConnection()) {
            try (PreparedStatement cmd = connection.prepareStatement(sql)) {
                cmd.setString(0, guild_id);
                cmd.setString(1, title);
                try (ResultSet results = cmd.executeQuery()) {
                    if (results.next()) content = results.getString("content");
                }
            }
        } catch (Exception error) {
            suzuya.errorTrace(error.getMessage(), error.getStackTrace());
        }
        return content;
    }

    public Boolean setTag(String authorID, String guildID, String title, String content) {
        String sql = "INSERT OR REPLACE INTO tags" +
                "(user_id, guild_id, title, content, timestamp)" +
                "VALUES" +
                "(?, ?, ?, ?, ?)";
        boolean status = false;
        try (Connection connection = pool.getConnection()) {
            try (PreparedStatement cmd = connection.prepareStatement(sql)) {
                cmd.setString(1, authorID);
                cmd.setString(2, guildID);
                cmd.setString(3, title);
                cmd.setString(4, content);
                cmd.setLong(5, Instant.now().getEpochSecond());
                int update = cmd.executeUpdate();
                if (update > 0) status = true;
            }
        } catch (Exception error) {
            suzuya.errorTrace(error.getMessage(), error.getStackTrace());
        }
        return status;
    }

    public Boolean deleteTag(String guildID, String title) {
        String sql = "DELETE FROM tags WHERE guild_id = ? AND title = ?";
        boolean status = false;
        try (Connection connection = pool.getConnection()) {
            try (PreparedStatement cmd = connection.prepareStatement(sql)) {
                cmd.setString(1, guildID);
                cmd.setString(2, title);
                int update = cmd.executeUpdate();
                if (update > 0) status = true;
            }
        } catch (Exception error) {
            suzuya.errorTrace(error.getMessage(), error.getStackTrace());
        }
        return status;
    }
}
