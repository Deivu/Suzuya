package suzuya.handler;

import suzuya.Config;
import java.sql.*;

public class SettingsHandler {
    Config config;
    Connection connection;

    public SettingsHandler(Config settings)
    {
        config = settings;
    }

    public void initDb(String db)
    {

        String location = "jdbc:sqlite:" + config.getDir() + db;
        try {
            connection = DriverManager.getConnection(location);
            if (connection != null) System.out.println("A new database has been created.");
        } catch (SQLException error) {
            error.printStackTrace();
        } finally {
            if (connection == null) {
                System.out.println("Database not found, exiting");
                System.exit(0);
            }
            try {
                String sql = String.format(
                        "CREATE TABLE IF NOT EXISTS settings(" +
                        "guild_id TEXT PRIMARY KEY," +
                        "prefix TEXT NOT NULL" +
                        ")"
                );
                PreparedStatement cmd = connection.prepareStatement(sql);
                cmd.execute();
            } catch (SQLException error) {
                error.printStackTrace();
                System.exit(0);
            }
        }
    }

    public void setDefaults(String guild_id)
    {
        String sql = "INSERT INTO settings (guild_id, prefix) VALUES (?, ?) ON CONFLICT(guild_id) DO UPDATE SET guild_id = guild_id";
        try {
            PreparedStatement cmd = connection.prepareStatement(sql);
            cmd.setString(1, guild_id);
            cmd.setString(2, config.getPrefix());
            cmd.executeUpdate();
        } catch (SQLException error) {
            error.printStackTrace();
        }
    }

    public void setPrefix(String guild_id, String prefix)
    {
        String sql = "INSERT INTO settings (guild_id, prefix) VALUES (?, ?)";
        try {
            PreparedStatement cmd = connection.prepareStatement(sql);
            cmd.setString(1, guild_id);
            cmd.setString(2, prefix);
            cmd.executeUpdate();
        } catch (SQLException error) {
            error.printStackTrace();
        }
    }

    public String getPrefix(String guild_id)
    {
        String prefix = config.getPrefix();
        String sql = "SELECT prefix FROM settings WHERE guild_id = ?";
        try {
            PreparedStatement cmd = connection.prepareStatement(sql);
            cmd.setString(1, guild_id);
            ResultSet results = cmd.executeQuery();
            while (results.next())
            {
                String customPrefix = results.getString("prefix");
                prefix = customPrefix;
            }
            results.close();
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return prefix;
    }
}
