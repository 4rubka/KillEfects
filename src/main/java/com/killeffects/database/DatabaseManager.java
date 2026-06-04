package com.killeffects.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class DatabaseManager {
    private final Plugin plugin;
    private HikariDataSource dataSource;
    private final Map<UUID, PlayerData> statsCache = new ConcurrentHashMap<>();

    public DatabaseManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void initialize(String type, String host, int port, String database, String username, String password) {
        HikariConfig config = new HikariConfig();
        
        if (type.equalsIgnoreCase("mysql")) {
            config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        } else {
            config.setJdbcUrl("jdbc:sqlite:" + plugin.getDataFolder() + "/database.db");
            config.setDriverClassName("org.sqlite.JDBC");
        }

        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(30));
        config.setKeepaliveTime(TimeUnit.MINUTES.toMillis(1));

        this.dataSource = new HikariDataSource(config);
        createTables();
        startBatchTask();
    }

    private void createTables() {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS killeffects_players (" +
                     "uuid VARCHAR(36) PRIMARY KEY, " +
                     "kills INT DEFAULT 0, " +
                     "highest_streak INT DEFAULT 0, " +
                     "selected_effect VARCHAR(64) DEFAULT 'none', " +
                     "random_mode BOOLEAN DEFAULT FALSE" +
                     ")")) {
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PlayerData getPlayerData(UUID uuid) {
        return statsCache.computeIfAbsent(uuid, PlayerData::new);
    }

    private void startBatchTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::saveAll, 12000L, 12000L); // Every 10 minutes
    }

    public void saveAll() {
        if (statsCache.isEmpty()) return;

        String query = "INSERT INTO killeffects_players (uuid, kills, highest_streak, selected_effect, random_mode) " +
                       "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                       "kills = VALUES(kills), highest_streak = VALUES(highest_streak), " +
                       "selected_effect = VALUES(selected_effect), random_mode = VALUES(random_mode)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            conn.setAutoCommit(false);
            for (PlayerData data : statsCache.values()) {
                ps.setString(1, data.getUuid().toString());
                ps.setInt(2, data.getKills());
                ps.setInt(3, data.getHighestStreak());
                ps.setString(4, data.getSelectedEffect());
                ps.setBoolean(5, data.isRandomMode());
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        saveAll();
        if (dataSource != null) {
            dataSource.close();
        }
    }

    public static class PlayerData {
        private final UUID uuid;
        private int kills;
        private int highest_streak;
        private String selectedEffect = "none";
        private boolean randomMode = false;

        public PlayerData(UUID uuid) { this.uuid = uuid; }
        public UUID getUuid() { return uuid; }
        public int getKills() { return kills; }
        public void addKill() { this.kills++; }
        public int getHighestStreak() { return highest_streak; }
        public void setHighestStreak(int streak) { if (streak > highest_streak) highest_streak = streak; }
        public String getSelectedEffect() { return selectedEffect; }
        public void setSelectedEffect(String selectedEffect) { this.selectedEffect = selectedEffect; }
        public boolean isRandomMode() { return randomMode; }
        public void setRandomMode(boolean randomMode) { this.randomMode = randomMode; }
    }
}
