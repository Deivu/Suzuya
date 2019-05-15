package suzuya.handler;

import suzuya.Config;

import java.sql.*;

public class SettingsHandler {
    private final Config config;
    private Connection connection;

    public SettingsHandler(Config settings) {
        config = settings;
    }

    public void initDb(String db) {
        String location = "jdbc:sqlite:" + config.getDir() + db;
        try {
            connection = DriverManager.getConnection(location);
            if (connection != null) System.out.println("Connected to Settings Database.");
        } catch (SQLException error) {
            error.printStackTrace();
        } finally {
            if (connection == null) {
                System.out.println("No connection to Settings Database. Exiting.");
                System.exit(0);
            }
            try {
                String sql = "CREATE TABLE IF NOT EXISTS settings(" +
                        "guild_id TEXT PRIMARY KEY," +
                        "prefix TEXT NOT NULL" +
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

    public void setDefaults(String guild_id) {
        String sql = "INSERT INTO settings (guild_id, prefix) VALUES (?, ?) ON CONFLICT(guild_id) DO UPDATE SET guild_id = guild_id";
        try {
            PreparedStatement cmd = connection.prepareStatement(sql);
            try {
                cmd.setString(1, guild_id);
                cmd.setString(2, config.getPrefix());
                cmd.executeUpdate();
            } catch (Exception error) {
                error.printStackTrace();
            } finally {
                cmd.close();
            }
        } catch (SQLException error) {
            error.printStackTrace();
        }
    }

    public void setDataString(String guild_id, String column, String data) {
        String sql = "UPDATE settings SET " + column + " = ? WHERE guild_id = ?";
        try {
            PreparedStatement cmd = connection.prepareStatement(sql);
            try {
                cmd.setString(1, data);
                cmd.setString(2, guild_id);
                cmd.executeUpdate();
            } catch (Exception error) {
                error.printStackTrace();
            } finally {
                cmd.close();
            }
        } catch (SQLException error) {
            error.printStackTrace();
        }
    }

    public String getDataString(String column, String guild_id) {
        String data = null;
        String sql = "SELECT " + column + " FROM settings WHERE guild_id = ?";
        try {
            PreparedStatement cmd = connection.prepareStatement(sql);
            try {
                cmd.setString(1, guild_id);
                ResultSet results = cmd.executeQuery();
                try {
                    if (results.next()) data = results.getString(column);
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
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return data;
    }
}
