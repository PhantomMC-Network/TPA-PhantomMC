package org.abbas.tPAPlugin.command;

import org.abbas.tPAPlugin.Utils.ColorUtil;
import org.abbas.tPAPlugin.config.MessageManager;
import org.abbas.tPAPlugin.manager.TPARequestManager;
import org.abbas.tPAPlugin.manager.TeleportManager;
import org.abbas.tPAPlugin.model.TPARequest;
import org.abbas.tPAPlugin.model.TPARequestType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPAAcceptCommand implements CommandExecutor {

    private final TPARequestManager requestManager;
    private final MessageManager messageManager;
    private final TeleportManager teleportManage;

    public TPAAcceptCommand(TPARequestManager requestManager, MessageManager messageManager, TeleportManager teleportManage) {
        this.requestManager = requestManager;
        this.messageManager = messageManager;
        this.teleportManage = teleportManage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtil.colorize("&cOnly players may use this command!"));
            return true;
        }
        if (!player.hasPermission("tpa.accept")) {
            messageManager.send(
                    player,
                    "messages.no-permission"
            );
            return true;
        }
        if (args.length != 0) {
            player.sendMessage(
                    ColorUtil.colorize(
                            "&cUsage: &7/tpaccept")
            );
            return true;
        }
        /**
         * get pending request
         */
        TPARequest request = requestManager.getRequest(player);
        if (request == null) {
            messageManager.send(
                    player,
                    "messages.no-pending-request"
            );
            return true;
        }

        /**
         * Cheack expiration
         */
        if (System.currentTimeMillis() >= request.getExpiresAt()) {
            requestManager.removeRequest(player);

            messageManager.send(
                    player,
                    "messages.request-expired"
            );
            return true;
        }

        Player requestSender = request.getSender();
        /**
         * request sender is offline
         */
        if (requestSender == null || !requestSender.isOnline()) {
            requestManager.removeRequest(player);

            messageManager.send(
                    player,
                    "messages.request-sender-offline"
            );
            return true;
        }
        Player playerToTeleport;
        Player destinationPlayer;
        /**
         * TPA
         * Example:
         * Jorge -> Alex
         * Jorge Wants teleport to Alex
         * Alex accepts.
         */
        if (request.getType() == TPARequestType.TPA) {
            playerToTeleport = requestSender;
            destinationPlayer = player;

            /**
             * TPAHERE
             */
        } else if (request.getType() == TPARequestType.TPA_HERE) {
            playerToTeleport = player;
            destinationPlayer = requestSender;
        } else {
            return true;
        }

        /**
         * remove request before teleporting
         */
        requestManager.removeRequest(player);

        /*
        start teleport
         */
        teleportManage.startTeleport(playerToTeleport,
               destinationPlayer.getLocation());

        /**
         * notify receiver
         */
        messageManager.sendWithPlaceholder(
                player,
                "messages.request-accepted",
                "%player%",
                requestSender.getName()
        );

        /**
         * notify sender
         */
        messageManager.sendWithPlaceholder(
                requestSender,
                "messages.request-accepted-sender",
                "%player%",
                player.getName()
        );

        return true;
    }
}
