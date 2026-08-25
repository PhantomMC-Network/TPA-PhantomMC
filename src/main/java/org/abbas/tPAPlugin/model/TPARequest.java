package org.abbas.tPAPlugin.model;

import org.bukkit.entity.Player;

public class TPARequest {

    private final Player sender;
    private final Player target;
    private final TPARequestType type;
    private final long createdAt;
    private final long expiresAt;

    public TPARequest(
            Player sender,
            Player target,
            TPARequestType type,
            long createdAt,
            long expiresAt
    ) {
        this.sender = sender;
        this.target = target;
        this.type = type;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public Player getSender() {
        return sender;
    }

    public Player getTarget() {
        return target;
    }

    public TPARequestType getType() {
        return type;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getExpiresAt() {
        return expiresAt;
    }
}