package org.unitedlands.unitedchat.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static org.unitedlands.unitedchat.UnitedChat.getMessage;

public class ClearChatCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        // Check permission
        if (!sender.hasPermission("united.chat.admin")) {
            sender.sendMessage(getMessage("message.no-perm"));
            return false;
        }
        for (int i = 0; i < 150; i++) {
            Bukkit.broadcast(Component.newline());
        }
        return true;
    }
}
