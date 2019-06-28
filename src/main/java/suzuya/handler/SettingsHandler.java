package suzuya.handler;

import org.h2.jdbcx.JdbcConnectionPool;
import suzuya.Config;
import suzuya.SuzuyaClient;
import suzuya.structures.Settings;
import java.sql.*;
import java.util.ArrayList;

public class SettingsHandler {
    private final SuzuyaClient suzuya;
    private final Config config;
    private final JdbcConnectionPool pool;

    public SettingsHandler(SuzuyaClient suzuya) {
        this.suzuya = suzuya;
        this.config = suzuya.config;
        this.pool = JdbcConnectionPool.create(
                "jdbc:h2:file:" + config.getDir() + "\\db\\SuzuyaSettings;MULTI_THREADED=1",
                "",
                ""
        );
        suzuya.SuzuyaLog.info("Connected to Settings Database.");
    }

    public void initDb() {
        String sql = "CREATE TABLE IF NOT EXISTS settings(" +
                "guild_id TEXT PRIMARY KEY," +
                "prefix TEXT NOT NULL," +
                "mod_log TEXT," +
                "auto_ban TEXT NOT NULL," +
                "verification_channel TEXT," +
                "silenced_role TEXT" +
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

    public void setDefaults(String guild_id) {
        String sql = "INSERT INTO settings (guild_id, prefix, auto_ban) VALUES (?, ?, ?) ON CONFLICT(guild_id) DO UPDATE SET guild_id = guild_id";
        try (Connection connection = pool.getConnection()) {
            try (PreparedStatement cmd = connection.prepareStatement(sql)) {
                cmd.setString(1, guild_id);
                cmd.setString(2, config.getPrefix());
                cmd.setString(3, "false");
                cmd.executeUpdate();
            }
        } catch (Exception error) {
            suzuya.errorTrace(error.getMessage(), error.getStackTrace());
        }
    }

    public void setDataString(String guild_id, String column, String data) {
        String sql = "UPDATE settings SET " + column + " = ? WHERE guild_id = ?";
        try (Connection connection = pool.getConnection()) {
            try (PreparedStatement cmd = connection.prepareStatement(sql)) {
                cmd.setString(1, data);
                cmd.setString(2, guild_id);
                cmd.executeUpdate();
            }
        } catch (Exception error) {
            suzuya.errorTrace(error.getMessage(), error.getStackTrace());
        }
    }

    @SuppressWarnings("unused")
    public ArrayList<Settings> getListSettings(String setting) {
        ArrayList<Settings> settings = new ArrayList<>();
        String sql = "SELECT * FROM settings WHERE " + setting + " = 'true'";
        try (Connection connection = pool.getConnection()) {
            try (PreparedStatement cmd = connection.prepareStatement(sql)) {
                try (ResultSet results = cmd.executeQuery()) {
                    if (results.next()) {
                        Settings _settings = new Settings();
                        _settings.guild_id = results.getString("guild_id");
                        _settings.prefix = results.getString("prefix");
                        _settings.mod_log = results.getString("mod_log");
                        _settings.auto_ban = results.getString("auto_ban");
                        _settings.verification_channel = results.getString("verification_channel");
                        _settings.silenced_role = results.getString("silenced_role");
                        _settings.isInit = true;
                        settings.add(_settings);
                    }
                }
            }
        } catch (Exception error) {
            suzuya.errorTrace(error.getMessage(), error.getStackTrace());
        }
        return settings;
    }

    public Settings getSettings(String guild_id) {
        Settings settings = new Settings();
        String sql = "SELECT * FROM settings WHERE guild_id = ?";
        try (Connection connection = pool.getConnection()) {
            try (PreparedStatement cmd = connection.prepareStatement(sql)) {
                cmd.setString(1, guild_id);
                try (ResultSet results = cmd.executeQuery()) {
                    if (results.next()) {
                        settings.guild_id = results.getString("guild_id");
                        settings.prefix = results.getString("prefix");
                        settings.mod_log = results.getString("mod_log");
                        settings.auto_ban = results.getString("auto_ban");
                        settings.verification_channel = results.getString("verification_channel");
                        settings.silenced_role = results.getString("silenced_role");
                        settings.isInit = true;
                    }
                }
            }
        } catch (Exception error) {
            suzuya.errorTrace(error.getMessage(), error.getStackTrace());
        }
        return settings.isInit ? settings : null;
    }
}
