package org.abbas.tPAPlugin.command;

import org.abbas.tPAPlugin.Utils.ColorUtil;
import org.abbas.tPAPlugin.config.MessageManager;
import org.abbas.tPAPlugin.model.TPARequestType;
import org.abbas.tPAPlugin.service.TPARequestService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPACommand implements CommandExecutor {

    private final TPARequestService requestService;
    private final MessageManager messageManager;

    public TPACommand(
            TPARequestService requestService,
            MessageManager messageManager
    ) {
        this.requestService = requestService;
        this.messageManager = messageManager;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(
                    ColorUtil.colorize(
                            "&cOnly players can use this command!"
                    )
            );
            return true;
        }

        if (!player.hasPermission("tpa.use")) {
            messageManager.send(
                    player,
                    "messages.no-permission"
            );
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(
                    ColorUtil.colorize(
                            "&cUsage: &7/tpa <player>"
                    )
            );
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);

        if (target == null) {
            messageManager.sendWithPlaceholder(
                    player,
                    "messages.player-not-found",
                    "%player%",
                    args[0]
            );
            return true;
        }

        // Send TPA request through the Service
        requestService.sendRequest(player, target, TPARequestType.TPA);

        return true;
    }
}