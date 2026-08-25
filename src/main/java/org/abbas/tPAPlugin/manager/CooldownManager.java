package org.abbas.tPAPlugin.manager;

import org.abbas.tPAPlugin.TPAPlugin;
import org.abbas.tPAPlugin.config.ConfigManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private final TPAPlugin plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final ConfigManager configManager;
    public CooldownManager(TPAPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }
    /**
     * Starts a cooldown for a player
     */
    public void startCooldown(Player player) {
        if (player == null) {
            return;
        }
        long cooldownSeconds = configManager.getCooldownSeconds();

        long expiresAt = System.currentTimeMillis()
                + (cooldownSeconds * 1000L);
        cooldowns.put(player.getUniqueId(), expiresAt);
    }
    /**
     * Checks whether a player is currently on cooldown.
     */
    public boolean isOnCooldown(Player player) {
        if (player == null) {
            return false;
        }
        UUID uuid = player.getUniqueId();
        Long expiresAt = cooldowns.get(uuid);
        if (expiresAt == null) {
            return false;
        }
        if (System.currentTimeMillis() >= expiresAt) {
            cooldowns.remove(uuid);
            return false;
        }
        return true;
    }
    /**
     * Gets the remaining cooldown in seconds.
     */
    public long getRemainingCooldown(Player player) {
        if (player == null) {
            return 0;
        }
        long expiresAt = cooldowns.get(player.getUniqueId());

        if (expiresAt <= 0) {
            return 0;
        }
        long remainingMillis = expiresAt - System.currentTimeMillis();

        if (remainingMillis <= 0) {
            cooldowns.remove(player.getUniqueId());
            return 0;
        }
        return (long) Math.ceil(remainingMillis / 1000.0);
    }
    /**
     * Removes a player's cooldown
     */
    public void removeCooldown(Player player) {
        if (player == null) {
            return;
        }
        cooldowns.remove(player.getUniqueId());
    }
    /**
     * Clears all cooldowns
     */
    public void clearAll() {
        cooldowns.clear();

    }

}
