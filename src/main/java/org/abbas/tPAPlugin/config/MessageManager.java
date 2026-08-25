package org.abbas.tPAPlugin.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.abbas.tPAPlugin.TPAPlugin;
import org.abbas.tPAPlugin.Utils.ColorUtil;
import org.bukkit.entity.Player;

public class MessageManager {

    private final TPAPlugin plugin;

    private final LegacyComponentSerializer serializer =
            LegacyComponentSerializer.legacyAmpersand();

    public MessageManager(TPAPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets a message from config.
     */
    public String get(String path) {

        String message = plugin.getConfig()
                .getString(path, "");

        return ColorUtil.colorize(message);
    }

    /**
     * Gets an Adventure Component from config.
     */
    public Component getComponent(String path) {

        String message = plugin.getConfig()
                .getString(path, "");

        return serializer.deserialize(message);
    }

    /**
     * Sends a normal chat message.
     */
    public void send(Player player, String path) {

        if (player == null) {
            return;
        }

        player.sendMessage(get(path));
    }

    /**
     * Gets a message with one placeholder.
     */
    public String getWithPlaceholder(
            String path,
            String placeholder,
            String value
    ) {

        return get(path)
                .replace(placeholder, value);
    }

    /**
     * Gets an Adventure Component with one placeholder.
     */
    public Component getComponentWithPlaceholder(
            String path,
            String placeholder,
            String value
    ) {

        String message = plugin.getConfig()
                .getString(path, "");

        message = message.replace(
                placeholder,
                value
        );

        return serializer.deserialize(message);
    }

    /**
     * Sends a chat message with one placeholder.
     */
    public void sendWithPlaceholder(
            Player player,
            String path,
            String placeholder,
            String value
    ) {

        if (player == null) {
            return;
        }

        player.sendMessage(
                getWithPlaceholder(path, placeholder, value)
        );
    }

    /**
     * Sends an Adventure ActionBar.
     */
    public void sendActionBar(
            Player player,
            String path
    ) {

        if (player == null) {
            return;
        }

        player.spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(get(path))
        );
    }

    /**
     * Clears the current ActionBar for a player.
     */
    public void clearActionBar(Player player) {

        if (player == null) {
            return;
        }

        player.spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText("")
        );
    }

    /**
     * Sends an Adventure ActionBar
     * with one placeholder.
     */
    public void sendActionBarWithPlaceholder(
            Player player,
            String path,
            String placeholder,
            String value
    ) {

        if (player == null) {
            return;
        }

        player.spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(
                        getWithPlaceholder(path, placeholder, value)
                )
        );
    }

    /**
     * Reloads configuration.
     */
    public void reload() {
        plugin.reloadConfig();
    }
}