package org.abbas.tPAPlugin.command;

import org.abbas.tPAPlugin.Utils.ColorUtil;
import org.abbas.tPAPlugin.config.MessageManager;
import org.abbas.tPAPlugin.manager.PlayerToggleManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPAToggleCommand implements CommandExecutor {
    private final PlayerToggleManager toggleManager;
    private final MessageManager messageManager;

    public TPAToggleCommand(PlayerToggleManager toggleManager, MessageManager messageManager) {
        this.toggleManager = toggleManager;
        this.messageManager = messageManager;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtil.colorize("&cOnly players can use this command!"));
            return true;
        }
        if (!player.hasPermission("tpa.toggle")) {
            messageManager.send(player,"messages.no-permission");
            return true;
        }
        if (args.length != 0) {
            player.sendMessage(ColorUtil.colorize("&cUsage: &e/tpatoggle"));
            return true;
        }
        boolean enabled = toggleManager.toggle(player);

        if (enabled) {
            messageManager.send(player, "messages.tpa-enabled");
        }else {
            messageManager.send(player, "messages.tpa-disabled");
        }

        return true;
    }
}
