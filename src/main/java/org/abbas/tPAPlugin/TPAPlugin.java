package org.abbas.tPAPlugin;

import org.abbas.tPAPlugin.command.*;
import org.abbas.tPAPlugin.config.ConfigManager;
import org.abbas.tPAPlugin.config.MessageManager;
import org.abbas.tPAPlugin.listeners.PlayerDamageListener;
import org.abbas.tPAPlugin.listeners.PlayerMoveListener;
import org.abbas.tPAPlugin.listeners.PlayerQuitListener;
import org.abbas.tPAPlugin.manager.CooldownManager;
import org.abbas.tPAPlugin.manager.PlayerToggleManager;
import org.abbas.tPAPlugin.manager.TPARequestManager;
import org.abbas.tPAPlugin.manager.TeleportManager;
import org.abbas.tPAPlugin.service.DatabaseService;
import org.abbas.tPAPlugin.service.SqliteService;
import org.abbas.tPAPlugin.service.TPARequestService;
import org.bukkit.plugin.java.JavaPlugin;

public final class TPAPlugin extends JavaPlugin {

    private static TPAPlugin instance;

    private ConfigManager configManager;
    private MessageManager messageManager;

    private TPARequestManager requestManager;
    private TeleportManager teleportManager;
    private CooldownManager cooldownManager;
    private PlayerToggleManager playerToggleManager;

    private TPARequestService requestService;

    private SqliteService sqliteService;
    private DatabaseService databaseService;

    @Override
    public void onEnable() {

        instance = this;

        /*
         * Load configuration
         */
        saveDefaultConfig();

        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);

        /*
         * Start SQLite database
         */
        sqliteService = new SqliteService(this);
        databaseService = new DatabaseService(sqliteService);
        databaseService.initialize();

        /*
         * Create Managers
         */
        playerToggleManager = new PlayerToggleManager(databaseService);

        cooldownManager = new CooldownManager(
                this,
                configManager
        );

        requestManager = new TPARequestManager(
                this,
                configManager,
                messageManager
        );

        teleportManager = new TeleportManager(
                this,
                configManager,
                messageManager
        );

        /*
         * Create Services
         */
        requestService = new TPARequestService(
                requestManager,
                cooldownManager,
                playerToggleManager,
                messageManager,
                databaseService
        );

        /*
         * Register Commands
         */
        getCommand("tpa").setExecutor(
                new TPACommand(
                        requestService,
                        messageManager
                )
        );

        getCommand("tpahere").setExecutor(
                new TPAHereCommand(
                        requestService,
                        messageManager
                )
        );

        getCommand("tpaccept").setExecutor(
                new TPAAcceptCommand(
                        requestManager,
                        messageManager,
                        teleportManager
                )
        );

        getCommand("tpdeny").setExecutor(
                new TpaDenyCommand(
                        messageManager,
                        requestManager
                )
        );

        getCommand("tpacancel").setExecutor(
                new TPACancelCommand(
                        messageManager,
                        requestManager
                )
        );

        getCommand("tpatoggle").setExecutor(
                new TPAToggleCommand(
                        playerToggleManager,
                        messageManager
                )
        );

        /*
         * Register Listeners
         */
        getServer().getPluginManager().registerEvents(
                new PlayerMoveListener(
                        configManager,
                        messageManager,
                        teleportManager
                ),
                this
        );

        getServer().getPluginManager().registerEvents(
                new PlayerDamageListener(
                        this,
                        configManager,
                        messageManager,
                        teleportManager
                ),
                this
        );

        getServer().getPluginManager().registerEvents(
                new PlayerQuitListener(
                        requestManager,
                        teleportManager
                ),
                this
        );

        getLogger().info("TPAPlugin enabled successfully!");
    }

    @Override
    public void onDisable() {

        /*
         * Clean pending teleports and their ActionBars
         */
        if (teleportManager != null) {
            teleportManager.shutdown();
        }

        /*
         * Close SQLite database
         */
        if (databaseService != null) {
            databaseService.close();
        }

        /*
         * Clean pending requests
         */
        if (requestManager != null) {
            requestManager.clearAll();
        }

        /*
         * Clean toggle states
         */
        if (playerToggleManager != null) {
            playerToggleManager.clearAll();
        }

        instance = null;

        getLogger().info("TPAPlugin disabled.");
    }

    public static TPAPlugin getInstance() {
        return instance;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    public SqliteService getSqliteService() {
        return sqliteService;
    }
}