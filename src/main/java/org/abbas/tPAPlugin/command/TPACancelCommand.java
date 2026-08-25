package org.abbas.tPAPlugin.command;

import org.abbas.tPAPlugin.TPAPlugin;
import org.abbas.tPAPlugin.Utils.ColorUtil;
import org.abbas.tPAPlugin.config.MessageManager;
import org.abbas.tPAPlugin.manager.TPARequestManager;
import org.abbas.tPAPlugin.model.TPARequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPACancelCommand implements CommandExecutor {
    private final MessageManager messageManager;
    private final TPARequestManager requestManager;

    public TPACancelCommand(
            MessageManager messageManager,
            TPARequestManager requestManager
    ) {
        this.messageManager = messageManager;
        this.requestManager = requestManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtil.colorize("&cOnly players can use this command!"));
            return true;
        }
        if (!player.hasPermission("tpa.cancel")) {
            sender.sendMessage(ColorUtil.colorize("&cNo permission!"));
            return true;
        }
        if (args.length != 0) {
            player.sendMessage(ColorUtil.colorize("&cUsage: &7/tpacancel"));
            return true;
        }
        TPARequest request = requestManager.getRequestFromSender(player);

        if (request == null) {
            messageManager.send(player,
                    "messages.no-request-to-cancel"
            );
            return true;
        }
        Player target = request.getTarget();

        requestManager.removeRequest(target);

        messageManager.sendWithPlaceholder(
                player,
                "messages.request-cancelled",
                "%player%",
                target.getName()
        );
        if (target.isOnline()) {
            messageManager.sendWithPlaceholder(
                    target,
                    "messages.request-cancelled-target",
                    "%player%",
                    target.getName()
            );
        }


        return true;
    }
}
