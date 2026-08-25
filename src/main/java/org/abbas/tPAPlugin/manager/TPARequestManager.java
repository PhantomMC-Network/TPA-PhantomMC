package org.abbas.tPAPlugin.manager;

import org.abbas.tPAPlugin.TPAPlugin;
import org.abbas.tPAPlugin.config.ConfigManager;
import org.abbas.tPAPlugin.config.MessageManager;
import org.abbas.tPAPlugin.model.TPARequest;
import org.abbas.tPAPlugin.model.TPARequestType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TPARequestManager {

    private final TPAPlugin plugin;
    private final Map<UUID, TPARequest> requests = new HashMap<>();
    private final ConfigManager configManager;
    private final MessageManager messageManager;
    public TPARequestManager(
            TPAPlugin plugin,
            ConfigManager configManager, MessageManager messageManager
    ) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageManager = messageManager;
    }

    public void sendRequest(
            Player sender,
            Player target,
            TPARequestType type
    ) {

        long createdAt = System.currentTimeMillis();

        long expireSeconds = configManager.getRequestExpireSeconds();

        long expiresAt = createdAt + (expireSeconds * 1000L);

        TPARequest request = new TPARequest(
                sender,
                target,
                type,
                createdAt,
                expiresAt
        );

        requests.put(target.getUniqueId(), request);

        // Automatically expire the request
        plugin.getServer().getScheduler().runTaskLater(
                plugin,
                () -> {

                    TPARequest currentRequest =
                            requests.get(target.getUniqueId());

                    // Make sure this is still the same request
                    if (currentRequest == null) {
                        return;
                    }

                    if (currentRequest != request) {
                        return;
                    }

                    // Remove expired request
                    requests.remove(target.getUniqueId());

                    // Notify sender
                    if (sender.isOnline()) {
                        messageManager.sendWithPlaceholder(
                                sender,
                                "messages.request-expired-sender",
                                "%player%",
                                target.getName()
                        );
                    }

                    // Notify target
                    if (target.isOnline()) {
                        messageManager.sendWithPlaceholder(
                                target,
                                "messages.request-expired-target",
                                "%player%",
                                sender.getName()
                        );
                    }

                },
                expireSeconds * 20L
        );
    }
    public TPARequest getRequestFromSender(Player sender) {

        if (sender == null) {
            return null;
        }

        UUID senderUUID = sender.getUniqueId();

        for (TPARequest request : requests.values()) {

            if (request.getSender()
                    .getUniqueId()
                    .equals(senderUUID)) {

                return request;
            }
        }

        return null;
    }

    public TPARequest getRequest(Player target) {
        return requests.get(target.getUniqueId());
    }

    public boolean hasRequest(Player target) {
        return requests.containsKey(target.getUniqueId());
    }

    public void removeRequest(Player target) {
        requests.remove(target.getUniqueId());
    }
    public void removeRequestsFrom(Player sender) {

        if (sender == null) {
            return;
        }

        UUID uuid = sender.getUniqueId();

        requests.entrySet().removeIf(
                entry -> entry.getValue()
                        .getSender()
                        .getUniqueId()
                        .equals(uuid)
        );
    }
    public void clearAll() {
        requests.clear();
    }
}