package org.abbas.tPAPlugin.Utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {
    private static final Pattern Hex_Color = Pattern.compile("&#?([A-Fa-f0-9]{6})");


    /**
     * Translates both Hex codes and legacy & codes into Minecraft colors.
     */
    public static String colorize(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }
        Matcher matcher = Hex_Color.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hexCode = matcher.group(1);
            // converts hex into required minecraft format
            matcher.appendReplacement(buffer, ChatColor.of("#" + hexCode).toString());
        }
        matcher.appendTail(buffer);
        String hexCode = buffer.toString();
        // Process standard legacy code like (&, #, etc)
        return ChatColor.translateAlternateColorCodes('&', hexCode);
    }
}
