package org.abbas.tPAPlugin.manager;

import org.abbas.tPAPlugin.service.DatabaseService;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerToggleManager {

    private final DatabaseService databaseService;
    private final Set<UUID> disabledPlayers = new HashSet<>();

    public PlayerToggleManager(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * Disables TPA requests for a player and persists the preference.
     */
    public void disable(Player player) {
        if (player == null) {
            return;
        }

        disabledPlayers.add(player.getUniqueId());
        databaseService.setTpaEnabled(player, false);
    }

    /**
     * Enables TPA requests for a player and persists the preference.
     */
    public void enable(Player player) {
        if (player == null) {
            return;
        }

        disabledPlayers.remove(player.getUniqueId());
        databaseService.setTpaEnabled(player, true);
    }

    /**
     * Toggles the player's TPA status.
     *
     * @return true when TPA is now enabled, false when it is disabled
     */
    public boolean toggle(Player player) {
        if (player == null) {
            return false;
        }

        if (isEnable(player)) {
            disable(player);
            return false;
        }

        enable(player);
        return true;
    }

    /**
     * Checks whether the player accepts TPA requests.
     */
    public boolean isEnable(Player player) {
        if (player == null) {
            return false;
        }

        UUID uuid = player.getUniqueId();

        if (disabledPlayers.contains(uuid)) {
            return false;
        }

        boolean enabled = databaseService.isTpaEnabled(player);
        if (!enabled) {
            disabledPlayers.add(uuid);
        }

        return enabled;
    }

    /**
     * Checks whether the player has disabled TPA requests.
     */
    public boolean isDisabled(Player player) {
        return player != null && !isEnable(player);
    }

    /**
     * Clears only the in-memory cache. Saved preferences remain in SQLite.
     */
    public void clearAll() {
        disabledPlayers.clear();
    }
}
