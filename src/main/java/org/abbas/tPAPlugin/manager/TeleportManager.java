package org.abbas.tPAPlugin.manager;

import org.abbas.tPAPlugin.TPAPlugin;
import org.abbas.tPAPlugin.config.ConfigManager;
import org.abbas.tPAPlugin.config.MessageManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager {

    private final Map<UUID, TeleportTask> pendingTeleports = new HashMap<>();
    private final Map<UUID, Location> teleportStartLocations = new HashMap<>();

    private final TPAPlugin plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;

    public TeleportManager(
            TPAPlugin plugin,
            ConfigManager configManager,
            MessageManager messageManager
    ) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageManager = messageManager;
    }

    /**
     * Starts a teleport with a warmup countdown.
     */
    public void startTeleport(Player player, Location destination) {

        if (player == null || destination == null) {
            return;
        }

        UUID uuid = player.getUniqueId();

        // Cancel an existing teleport first
        cancelTeleport(player);

        int warmupSeconds = configManager.getTeleportWarmupSeconds();

        // Store the location where teleport started
        teleportStartLocations.put(
                uuid,
                player.getLocation().clone()
        );

        TeleportTask teleportTask = new TeleportTask(
                player,
                destination,
                warmupSeconds
        );

        pendingTeleports.put(uuid, teleportTask);

        teleportTask.start();
    }

    /**
     * Cancels a player's pending teleport.
     */
    public void cancelTeleport(Player player) {

        if (player == null) {
            return;
        }

        UUID uuid = player.getUniqueId();

        TeleportTask teleportTask = pendingTeleports.remove(uuid);

        if (teleportTask != null) {
            teleportTask.cancel();
            messageManager.clearActionBar(player);
        }

        teleportStartLocations.remove(uuid);
    }

    /**
     * Checks whether a player has a pending teleport.
     */
    public boolean hasPendingTeleport(Player player) {

        if (player == null) {
            return false;
        }

        return pendingTeleports.containsKey(
                player.getUniqueId()
        );
    }

    /**
     * Gets the location where the teleport started.
     */
    public Location getTeleportStartLocation(Player player) {

        if (player == null) {
            return null;
        }

        return teleportStartLocations.get(
                player.getUniqueId()
        );
    }

    /**
     * Checks whether a player moved from the teleport start location.
     */
    public boolean hasMoved(Player player) {

        if (player == null) {
            return false;
        }

        Location startLocation =
                getTeleportStartLocation(player);

        if (startLocation == null) {
            return false;
        }

        Location currentLocation =
                player.getLocation();

        if (startLocation.getWorld() != currentLocation.getWorld()) {
            return true;
        }

        return startLocation.getX() != currentLocation.getX()
                || startLocation.getY() != currentLocation.getY()
                || startLocation.getZ() != currentLocation.getZ();
    }

    /**
     * Cancels all pending teleports.
     */
    public void shutdown() {

        for (TeleportTask task : pendingTeleports.values()) {
            task.clearActionBar();
            task.cancel();
        }

        pendingTeleports.clear();
        teleportStartLocations.clear();
    }

    /**
     * Represents one teleport countdown.
     */
    private class TeleportTask {

        private final Player player;
        private final Location destination;
        private int secondsLeft;

        private BukkitTask countdownTask;

        public TeleportTask(
                Player player,
                Location destination,
                int secondsLeft
        ) {
            this.player = player;
            this.destination = destination.clone();
            this.secondsLeft = secondsLeft;
        }

        /**
         * Starts the countdown.
         */
        public void start() {

            countdownTask = plugin.getServer()
                    .getScheduler()
                    .runTaskTimer(
                            plugin,
                            this::tick,
                            0L,
                            20L
                    );
        }

        /**
         * Runs every second.
         */
        private void tick() {

            if (!player.isOnline()) {
                cancelTeleport(player);
                return;
            }

            /*
             * Show countdown ActionBar.
             */
            if (secondsLeft > 0) {

                messageManager.sendActionBarWithPlaceholder(
                        player,
                        "messages.teleport-actionbar",
                        "%seconds%",
                        String.valueOf(secondsLeft)
                );

                secondsLeft--;
                return;
            }

            /*
             * Countdown finished.
             */
            finish();
        }

        /**
         * Finishes the teleport.
         */
        private void finish() {

            UUID uuid = player.getUniqueId();

            pendingTeleports.remove(uuid);
            teleportStartLocations.remove(uuid);

            if (countdownTask != null) {
                countdownTask.cancel();
                countdownTask = null;
            }

            if (!player.isOnline()) {
                return;
            }

            player.teleport(destination);

            messageManager.sendActionBar(
                    player,
                    "messages.teleport-actionbar-success"
            );
        }

        /**
         * Clears this player's countdown ActionBar.
         */
        public void clearActionBar() {
            if (player.isOnline()) {
                messageManager.clearActionBar(player);
            }
        }

        /**
         * Cancels this teleport task.
         */
        public void cancel() {

            if (countdownTask != null) {
                countdownTask.cancel();
                countdownTask = null;
            }
        }
    }
}