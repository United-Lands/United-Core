package org.unitedlands.items.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Logger {
    public static void log(String msg) {
        sendToConsole("&b[&c&lUnited&f&lItems&b]&r " + msg);
    }

    private static void sendToConsole(String msg) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }
}
