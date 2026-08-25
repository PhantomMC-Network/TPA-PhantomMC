package org.abbas.tPAPlugin.command;

import org.abbas.tPAPlugin.Utils.ColorUtil;
import org.abbas.tPAPlugin.config.MessageManager;
import org.abbas.tPAPlugin.manager.TPARequestManager;
import org.abbas.tPAPlugin.model.TPARequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaDenyCommand implements CommandExecutor {
    private final MessageManager messageManager;
    private final TPARequestManager RequestManager;

    public TpaDenyCommand(MessageManager messageManager, TPARequestManager requestManager) {
        this.messageManager = messageManager;
        RequestManager = requestManager;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(
                    ColorUtil.colorize(
                            "&cOnly players can use this command!"
                    )
            );
            return true;
        }
        if (player.hasPermission("tpa.deny")) {
            messageManager.send(
                    player,
                    "messages.no-permission"
            );
            return true;
        }
        if (args.length != 0) {
            player.sendMessage(ColorUtil.colorize("&c&lUsage: &7/tpadeny"));
            return true;
        }
        TPARequest request = RequestManager.getRequest(player);
        if (request == null) {
            messageManager.send(
                    player,
                    "messages.no-pending-request"
            );
            return true;
        }
        Player requestSender = request.getSender();

        /*
        remove request
        important:
        after this point /tpaccept or /tpadeny
        can not use this same request
         */
        RequestManager.removeRequest(player);
        /**
         * notify player remove request
         *
         * like Jorge "You denied Alex Request"
         */
        messageManager.sendWithPlaceholder(player,
                "messages.request-denied",
                "%player%",
                requestSender.getName()
        );
        /**
         * notify player send request
         * Alex:
         * "Jorge denied your request"
         */
        if (requestSender.isOnline()) {
            messageManager.sendWithPlaceholder(
                    requestSender,
                    "messages.request-denied-sender",
                    "%player%",
                    requestSender.getName()
            );
        }

        return true;
    }
}
