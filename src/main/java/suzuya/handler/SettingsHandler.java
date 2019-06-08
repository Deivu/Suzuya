package suzuya.handler;

import suzuya.Config;
import suzuya.structures.Settings;

import java.sql.*;

public class SettingsHandler {
    private final Config config;
    private Connection connection;

    public SettingsHandler(Config config) {
        this.config = config;
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
            //noinspection TryFinallyCanBeTryWithResources
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


    @SuppressWarnings("unused")
    public void setDataString(String guild_id, String column, String data) {
        String sql = "UPDATE settings SET " + column + " = ? WHERE guild_id = ?";
        try {
            PreparedStatement cmd = connection.prepareStatement(sql);
            //noinspection TryFinallyCanBeTryWithResources
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

    public Settings getSettings(String guild_id) {
        Settings settings = new Settings();
        String sql = "SELECT * FROM settings WHERE guild_id = ?";
        try {
            PreparedStatement cmd = connection.prepareStatement(sql);
            //noinspection TryFinallyCanBeTryWithResources
            try {
                cmd.setString(1, guild_id);
                ResultSet results = cmd.executeQuery();
                //noinspection TryFinallyCanBeTryWithResources
                try {
                    if (results.next()) {
                        settings.prefix = results.getString("prefix");
                        settings.isInit = true;
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
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return settings.isInit ? settings : null;
    }
}
