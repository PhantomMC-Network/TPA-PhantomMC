package org.abbas.tPAPlugin.listeners;

import org.abbas.tPAPlugin.TPAPlugin;
import org.abbas.tPAPlugin.config.MessageManager;
import org.abbas.tPAPlugin.manager.TPARequestManager;
import org.abbas.tPAPlugin.manager.TeleportManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final TPARequestManager requestManager;
    private final TeleportManager teleportManager;

    public PlayerQuitListener(
            TPARequestManager requestManager,
            TeleportManager teleportManager
    ) {
        this.requestManager = requestManager;
        this.teleportManager = teleportManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        teleportManager.cancelTeleport(player);

        requestManager.removeRequest(player);
        requestManager.removeRequestsFrom(player);
    }
}
