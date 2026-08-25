package org.abbas.tPAPlugin.listeners;

import org.abbas.tPAPlugin.TPAPlugin;
import org.abbas.tPAPlugin.config.ConfigManager;
import org.abbas.tPAPlugin.config.MessageManager;
import org.abbas.tPAPlugin.manager.TeleportManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageListener implements Listener {

    private final TPAPlugin plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;
    private final TeleportManager teleportManager;

    public PlayerDamageListener(
            TPAPlugin plugin,
            ConfigManager configManager,
            MessageManager messageManager,
            TeleportManager teleportManager
    ) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageManager = messageManager;
        this.teleportManager = teleportManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageEvent event) {

        if (!configManager.isCancelOnDamage()) {
            return;
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!teleportManager.hasPendingTeleport(player)) {
            return;
        }

        teleportManager.cancelTeleport(player);

        messageManager.send(
                player,
                "messages.teleport-cancelled"
        );
    }
}