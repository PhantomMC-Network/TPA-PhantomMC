package org.abbas.tPAPlugin.service;

import org.abbas.tPAPlugin.Utils.PlayerUtil;
import org.abbas.tPAPlugin.config.MessageManager;
import org.abbas.tPAPlugin.manager.CooldownManager;
import org.abbas.tPAPlugin.manager.PlayerToggleManager;
import org.abbas.tPAPlugin.manager.TPARequestManager;
import org.abbas.tPAPlugin.model.TPARequestType;
import org.bukkit.entity.Player;

public class TPARequestService {

    private final TPARequestManager requestManager;
    private final CooldownManager cooldownManager;
    private final PlayerToggleManager playerToggleManager;
    private final MessageManager messageManager;
    private final DatabaseService databaseService;

    public TPARequestService(
        TPARequestManager requestManager,
            CooldownManager cooldownManager,
            PlayerToggleManager playerToggleManager,
            MessageManager messageManager,
            DatabaseService databaseService
    ) {
        this.requestManager = requestManager;
        this.cooldownManager = cooldownManager;
        this.playerToggleManager = playerToggleManager;
        this.messageManager = messageManager;
        this.databaseService = databaseService;
    }

    public boolean sendRequest(
            Player sender,
            Player target,
            TPARequestType type
    ) {

        if (sender == null || target == null || type == null) {
            return false;
        }

        // Cannot send request to yourself
        if (PlayerUtil.isSamePlayer(sender, target)) {
            messageManager.send(
                    sender,
                    "messages.cannot-target-self"
            );
            return false;
        }

        // Target must have TPA enabled
        if (!playerToggleManager.isEnable(target)) {
            messageManager.send(
                    sender,
                    "messages.tpa-disabled"
            );
            return false;
        }

        // Check cooldown
        if (cooldownManager.isOnCooldown(sender)) {

            long remaining =
                    cooldownManager.getRemainingCooldown(sender);

            messageManager.sendWithPlaceholder(
                    sender,
                    "messages.cooldown",
                    "%seconds%",
                    String.valueOf(remaining)
            );

            return false;
        }

        // Target already has a request
        if (requestManager.hasRequest(target)) {
            messageManager.send(
                    sender,
                    "messages.request-already-exists"
            );
            return false;
        }

        // Create request
        requestManager.sendRequest(
                sender,
                target,
                type
        );

        databaseService.logRequest(
                sender,
                target,
                type,
                "PENDING"
        );

        // Start cooldown
        cooldownManager.startCooldown(sender);

        // Notify sender
        messageManager.sendWithPlaceholder(
                sender,
                "messages.request-sent",
                "%player%",
                target.getName()
        );

        // Notify target
        messageManager.sendWithPlaceholder(
                target,
                "messages.request-received",
                "%player%",
                sender.getName()
        );

        return true;
    }
}