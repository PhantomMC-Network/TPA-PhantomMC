package org.abbas.tPAPlugin.Utils;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerUtil {
    private PlayerUtil() {
        //Utility Class
    }
    /**
     * Gets an online player by exact name
     * @param name Player name
     * @return Player if online, otherwise null
     */
    public static Player getOnlinePlayer(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        return Bukkit.getPlayer(name);
    }
    /**
     * Check player is online
     *
     * @param player Player
     * @return true if the player is online
     */
    public static boolean isOnline(Player player) {
        return player != null && player.isOnline();
    }
    /**
     * Checks if two players are the same
     * @param player1 First player
     * @param player2 Second player
     * @return true if both players are the same
     */
    public static boolean isSamePlayer(Player player1, Player player2) {
        if (player1 == null || player2 == null) {
            return false;
        }
        return player1.getUniqueId().equals(player2.getUniqueId());
    }

}
