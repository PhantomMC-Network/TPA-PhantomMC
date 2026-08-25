package org.abbas.tPAPlugin.listeners;

import org.abbas.tPAPlugin.config.ConfigManager;
import org.abbas.tPAPlugin.config.MessageManager;
import org.abbas.tPAPlugin.manager.TeleportManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private final ConfigManager configManager;
    private final MessageManager messageManager;
    private final TeleportManager teleportManager;

    public PlayerMoveListener(
            ConfigManager configManager,
            MessageManager messageManager,
            TeleportManager teleportManager
    ) {
        this.configManager = configManager;
        this.messageManager = messageManager;
        this.teleportManager = teleportManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        if (!configManager.isCancelOnMove()) {
            return;
        }

        Player player = event.getPlayer();

        if (!teleportManager.hasPendingTeleport(player)) {
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null) {
            return;
        }

        // Ignore head rotation.
        // Cancel only when X/Y/Z actually changes.
        if (from.getX() == to.getX()
                && from.getY() == to.getY()
                && from.getZ() == to.getZ()) {
            return;
        }

        teleportManager.cancelTeleport(player);

        messageManager.send(
                player,
                "messages.teleport-cancelled"
        );
    }
}