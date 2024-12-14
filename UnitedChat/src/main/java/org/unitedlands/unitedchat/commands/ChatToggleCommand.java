package org.unitedlands.unitedchat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.unitedlands.unitedchat.player.ChatFeature;
import org.unitedlands.unitedchat.player.ChatPlayer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.component;
import static org.unitedlands.unitedchat.UnitedChat.getMessage;

public class ChatToggleCommand implements TabExecutor {
    // Tab completions.
    private static final List<String> CHAT_FEATURE_TAB_COMPLETES = Arrays.asList("prefixes", "ranks", "broadcasts", "games", "gradients");
    private static final List<String> TOGGLES = Arrays.asList("on", "off");

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return CHAT_FEATURE_TAB_COMPLETES;
        } else if (args.length == 2) {
            return TOGGLES;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Ensure the sender is a player.
        if (!(sender instanceof Player player))  {
            sender.sendMessage(getMessage("messages.only-players"));
            return true;
        }
        // Check valid argument length.
        if (args.length != 2) {
            player.sendMessage(getMessage("messages.chat-toggle-command"));
            return true;
        }
        // Parse chat feature.
        ChatFeature feature;
        try {
            feature = ChatFeature.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(getMessage("messages.invalid-feature"));
            return true;
        }
        // Parse the toggle option.
        boolean toggle;
        if ("on".equalsIgnoreCase(args[1])) {
            toggle = true;
        } else if ("off".equalsIgnoreCase(args[1])) {
            toggle = false;
        } else {
            player.sendMessage(getMessage("messages.invalid-toggle"));
            return true;
        }
        // Toggle the chat feature for the player
        ChatPlayer chatPlayer = new ChatPlayer(player.getUniqueId());
        chatPlayer.toggleChatFeature(feature, toggle);

        // Send success message
        player.sendMessage(getMessage("messages.toggled-feature",
                component("feature", text(feature.name().toLowerCase())),
                component("toggle", text(args[1].toLowerCase()))
        ));
        return true;
    }
}
