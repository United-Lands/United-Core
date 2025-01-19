package org.unitedlands.items.tools;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class GamemasterTools extends CustomTool {

    private final Plugin plugin;
    private final String toolBrokenMessage;
    private final Map<Player, Long> messageCooldowns;

    public GamemasterTools(Plugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.toolBrokenMessage = config.getString("messages.tool-broken");
        this.messageCooldowns = new HashMap<>();
    }

    // Check the item's durability and broken state.
    private void updateToolState(ItemStack item) {
        if (!item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable)) {
            return;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey brokenKey = new NamespacedKey(plugin, "broken");

        CustomStack customStack = CustomStack.byItemStack(item);
        if (customStack == null) {
            return;
        }

        int currentDurability = customStack.getDurability();

        // Mark as broken if durability falls below 10.
        if (currentDurability <= 10) {
            container.set(brokenKey, PersistentDataType.BYTE, (byte) 1); // Mark as broken
        } else {
            // Remove the broken tag if durability is above 10.
            container.remove(brokenKey);
        }

        // Update the metadata.
        item.setItemMeta(meta);
    }

    @Override
    public void handleBlockBreak(Player player, BlockBreakEvent event) {
        ItemStack item = player.getInventory().getItemInMainHand();

        // Update durability and broken state.
        updateToolState(item);

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey brokenKey = new NamespacedKey(plugin, "broken");

        // Check if the item is broken and cancel the event if true.
        if (container.has(brokenKey, PersistentDataType.BYTE) &&
                container.getOrDefault(brokenKey, PersistentDataType.BYTE, (byte) 0) == 1) {
            event.setCancelled(true);
            sendWarningMessage(player);
        }
    }

    @Override
    public void handleInteract(Player player, PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        // Update durability and broken state.
        updateToolState(item);

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey brokenKey = new NamespacedKey(plugin, "broken");

        // Check if the tool is broken and cancel the event if true.
        if (container.has(brokenKey, PersistentDataType.BYTE) &&
                container.getOrDefault(brokenKey, PersistentDataType.BYTE, (byte) 0) == 1) {
            event.setCancelled(true);
            sendWarningMessage(player);
        }
    }

    @Override
    public void handleEntityDamage(Player player, EntityDamageByEntityEvent event) {
        ItemStack item = player.getInventory().getItemInMainHand();

        // Update durability and broken state.
        updateToolState(item);

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey brokenKey = new NamespacedKey(plugin, "broken");

        // Check if the tool is broken and cancel the event if true.
        if (container.has(brokenKey, PersistentDataType.BYTE) &&
                container.getOrDefault(brokenKey, PersistentDataType.BYTE, (byte) 0) == 1) {
            event.setCancelled(true);
            sendWarningMessage(player);
        }
    }

    private void sendWarningMessage(Player player) {
        long currentTime = System.currentTimeMillis();
        long lastMessageTime = messageCooldowns.getOrDefault(player, 0L);
        // Check if the cooldown has finished
        if (currentTime - lastMessageTime >= 5000) {
            player.sendMessage(toolBrokenMessage);
            messageCooldowns.put(player, currentTime);
        }
    }
}
