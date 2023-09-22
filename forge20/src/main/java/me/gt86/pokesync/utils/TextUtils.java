package me.gt86.pokesync.utils;

import net.md_5.bungee.api.ChatColor;

public class TextUtils {

    public static String PREFIX = "&8[&6PokeSync&8]&f ";

    public static String parseColour(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
