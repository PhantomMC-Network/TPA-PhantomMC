package org.abbas.tPAPlugin.config;

import org.abbas.tPAPlugin.TPAPlugin;

public class ConfigManager {

    private final TPAPlugin plugin;

    public ConfigManager(TPAPlugin plugin) {
        this.plugin = plugin;
    }

    public int getRequestExpireSeconds() {
        return plugin.getConfig().getInt(
                "settings.request-expire-seconds",
                60
        );
    }

    public int getTeleportWarmupSeconds() {
        return plugin.getConfig().getInt(
                "settings.teleport-warmup-seconds",
                5
        );
    }

    public int getCooldownSeconds() {
        return plugin.getConfig().getInt(
                "settings.cooldown-seconds",
                30
        );
    }

    public boolean isCancelOnMove() {
        return plugin.getConfig().getBoolean(
                "settings.cancel-on-move",
                true
        );
    }

    public boolean isCancelOnDamage() {
        return plugin.getConfig().getBoolean(
                "settings.cancel-on-damage",
                true
        );
    }

    public void reload() {
        plugin.reloadConfig();
    }
}