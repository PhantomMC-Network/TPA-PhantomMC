package org.abbas.tPAPlugin.service;

import org.abbas.tPAPlugin.TPAPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Low-level SQLite connection service.
 *
 * <p>This class owns one connection for the plugin lifecycle. Higher-level
 * database operations belong in {@link DatabaseService}.</p>
 */
public final class SqliteService implements AutoCloseable {

    private final TPAPlugin plugin;
    private Connection connection;

    public SqliteService(TPAPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Opens the SQLite database in the plugin data folder.
     */
    public void connect() {
        if (isConnected()) {
            return;
        }

        try {
            Class.forName("org.sqlite.JDBC");

            if (!plugin.getDataFolder().exists()
                    && !plugin.getDataFolder().mkdirs()) {
                throw new SQLException(
                        "Could not create plugin data folder: "
                                + plugin.getDataFolder().getAbsolutePath()
                );
            }

            String databaseFile = plugin.getConfig().getString(
                    "database.file",
                    "database.db"
            );

            File database = new File(plugin.getDataFolder(), databaseFile);
            connection = DriverManager.getConnection(
                    "jdbc:sqlite:" + database.getAbsolutePath()
            );

            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON");
                statement.execute("PRAGMA busy_timeout = 3000");
            }

            plugin.getLogger().info(
                    "SQLite database connected: " + database.getName()
            );
        } catch (ClassNotFoundException | SQLException exception) {
            throw new IllegalStateException(
                    "Could not connect to SQLite database",
                    exception
            );
        }
    }

    public Connection getConnection() {
        if (!isConnected()) {
            throw new IllegalStateException(
                    "SQLite is not connected. Call connect() first."
            );
        }

        return connection;
    }

    public void logError(String message, Throwable exception) {
        plugin.getLogger().warning(
                message + ": " + exception.getMessage()
        );
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException exception) {
            return false;
        }
    }

    @Override
    public void close() {
        if (!isConnected()) {
            return;
        }

        try {
            connection.close();
            plugin.getLogger().info("SQLite database connection closed.");
        } catch (SQLException exception) {
            plugin.getLogger().warning(
                    "Could not close SQLite database: "
                            + exception.getMessage()
            );
        } finally {
            connection = null;
        }
    }
}

