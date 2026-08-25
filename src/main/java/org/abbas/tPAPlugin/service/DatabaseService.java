package org.abbas.tPAPlugin.service;

import org.abbas.tPAPlugin.model.TPARequestType;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * High-level database operations used by TPAPlugin.
 */
public final class DatabaseService implements AutoCloseable {

    private final SqliteService sqliteService;

    public DatabaseService(SqliteService sqliteService) {
        this.sqliteService = sqliteService;
    }

    /**
     * Connects to SQLite and creates the plugin tables if they do not exist.
     */
    public void initialize() {
        sqliteService.connect();

        try (Statement statement = connection().createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS players (
                        uuid TEXT PRIMARY KEY,
                        name TEXT NOT NULL,
                        tpa_enabled INTEGER NOT NULL DEFAULT 1,
                        first_seen INTEGER NOT NULL,
                        last_seen INTEGER NOT NULL
                    )
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS tpa_requests (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        sender_uuid TEXT NOT NULL,
                        sender_name TEXT NOT NULL,
                        target_uuid TEXT NOT NULL,
                        target_name TEXT NOT NULL,
                        request_type TEXT NOT NULL,
                        status TEXT NOT NULL,
                        created_at INTEGER NOT NULL,
                        completed_at INTEGER
                    )
                    """);

            statement.executeUpdate("""
                    CREATE INDEX IF NOT EXISTS idx_tpa_requests_sender
                    ON tpa_requests(sender_uuid)
                    """);

            statement.executeUpdate("""
                    CREATE INDEX IF NOT EXISTS idx_tpa_requests_target
                    ON tpa_requests(target_uuid)
                    """);
        } catch (SQLException exception) {
            throw databaseException("Could not initialize database tables", exception);
        }
    }

    /**
     * Registers a player without changing the player's saved TPA preference.
     */
    public void registerPlayer(Player player) {
        if (player == null) {
            return;
        }

        long now = System.currentTimeMillis();

        try (PreparedStatement statement = connection().prepareStatement("""
                INSERT INTO players(uuid, name, tpa_enabled, first_seen, last_seen)
                VALUES (?, ?, 1, ?, ?)
                ON CONFLICT(uuid) DO UPDATE SET
                    name = excluded.name,
                    last_seen = excluded.last_seen
                """)) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, player.getName());
            statement.setLong(3, now);
            statement.setLong(4, now);
            statement.executeUpdate();
        } catch (SQLException exception) {
            logDatabaseError("Could not register player " + player.getName(), exception);
        }
    }

    /**
     * Returns the saved TPA preference. New players are enabled by default.
     */
    public boolean isTpaEnabled(Player player) {
        if (player == null) {
            return true;
        }

        registerPlayer(player);

        try (PreparedStatement statement = connection().prepareStatement(
                "SELECT tpa_enabled FROM players WHERE uuid = ?"
        )) {
            statement.setString(1, player.getUniqueId().toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                return !resultSet.next() || resultSet.getInt("tpa_enabled") == 1;
            }
        } catch (SQLException exception) {
            logDatabaseError("Could not read TPA preference", exception);
            return true;
        }
    }

    /**
     * Saves whether a player accepts incoming TPA requests.
     */
    public void setTpaEnabled(Player player, boolean enabled) {
        if (player == null) {
            return;
        }

        long now = System.currentTimeMillis();

        try (PreparedStatement statement = connection().prepareStatement("""
                INSERT INTO players(uuid, name, tpa_enabled, first_seen, last_seen)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT(uuid) DO UPDATE SET
                    name = excluded.name,
                    tpa_enabled = excluded.tpa_enabled,
                    last_seen = excluded.last_seen
                """)) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, player.getName());
            statement.setInt(3, enabled ? 1 : 0);
            statement.setLong(4, now);
            statement.setLong(5, now);
            statement.executeUpdate();
        } catch (SQLException exception) {
            logDatabaseError("Could not save TPA preference", exception);
        }
    }

    /**
     * Stores a request event for future statistics or administration tools.
     */
    public void logRequest(
            Player sender,
            Player target,
            TPARequestType requestType,
            String status
    ) {
        if (sender == null || target == null || requestType == null) {
            return;
        }

        try (PreparedStatement statement = connection().prepareStatement("""
                INSERT INTO tpa_requests(
                    sender_uuid,
                    sender_name,
                    target_uuid,
                    target_name,
                    request_type,
                    status,
                    created_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """)) {
            statement.setString(1, sender.getUniqueId().toString());
            statement.setString(2, sender.getName());
            statement.setString(3, target.getUniqueId().toString());
            statement.setString(4, target.getName());
            statement.setString(5, requestType.name());
            statement.setString(6, status == null ? "UNKNOWN" : status);
            statement.setLong(7, System.currentTimeMillis());
            statement.executeUpdate();
        } catch (SQLException exception) {
            logDatabaseError("Could not save TPA request log", exception);
        }
    }

    public Connection connection() {
        return sqliteService.getConnection();
    }

    @Override
    public void close() {
        sqliteService.close();
    }

    private void logDatabaseError(String message, SQLException exception) {
        sqliteService.logError(message, exception);
    }

    private IllegalStateException databaseException(
            String message,
            SQLException exception
    ) {
        return new IllegalStateException(message, exception);
    }
}

