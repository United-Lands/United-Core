package org.unitedlands.items.armours;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class GamemasterArmour extends CustomArmour {

    private final Plugin plugin;
    private final String armourBrokenMessage;
    private final Map<Player, Long> messageCooldowns;

    public GamemasterArmour(Plugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.armourBrokenMessage = config.getString("messages.armour-broken");
        this.messageCooldowns = new HashMap<>();
    }

    private void updateArmourState(ItemStack item) {
        // Get the custom item and check durability.
        CustomStack customStack = CustomStack.byItemStack(item);
        if (customStack == null) {
            return; // Skip non-custom items.
        }

        int currentDurability = customStack.getDurability();

        // Get item metadata.
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey brokenKey = new NamespacedKey(plugin, "broken");

        if (currentDurability <= 10) {
            // Mark as 'broken' and apply the 'unbreakable' tag.
            container.set(brokenKey, PersistentDataType.BYTE, (byte) 1); // Mark as broken.
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        } else {
            // Remove the 'broken' tag and unbreakable status if durability is above 10.
            container.remove(brokenKey);
            meta.setUnbreakable(false);
            item.setItemMeta(meta);
        }
    }

    private boolean isArmourBroken(ItemMeta meta) {
        if (meta == null) {
            return false;
        }

        NamespacedKey brokenKey = new NamespacedKey(plugin, "broken");
        PersistentDataContainer container = meta.getPersistentDataContainer();
        Byte isBroken = container.get(brokenKey, PersistentDataType.BYTE);
        return isBroken != null && isBroken == 1;
    }

    @Override
    public void handlePlayerDamage(Player player, EntityDamageEvent event) {
        // Get the player's armour contents.
        ItemStack[] armourContents = player.getInventory().getArmorContents();
        boolean hasBrokenArmour = false;

        // Iterate through each armour piece and update its state.
        for (ItemStack armourPiece : armourContents) {
            if (armourPiece != null && armourPiece.hasItemMeta()) {
                // Update the armour state: durability and metadata tags.
                updateArmourState(armourPiece);

                // Check if the armour is broken.
                if (isArmourBroken(armourPiece.getItemMeta())) {
                    hasBrokenArmour = true;
                }
            }
        }

        // Double the damage if the player has broken armour.
        if (hasBrokenArmour) {
            double originalDamage = event.getDamage();
            event.setDamage(originalDamage * 2);
            sendWarningMessage(player);
        }
    }

    @Override
    public void handleExpPickup(Player player, ExperienceOrb experienceOrb) {
        // Get the player's armour contents
        ItemStack[] armourContents = player.getInventory().getArmorContents();

        // Iterate through each armour piece and remove the 'unbreakable' tag
        for (ItemStack armourPiece : armourContents) {
            if (armourPiece != null && armourPiece.hasItemMeta()) {
                ItemMeta meta = armourPiece.getItemMeta();
                if (meta != null && meta.isUnbreakable()) {
                    meta.setUnbreakable(false); // Remove the unbreakable tag
                    armourPiece.setItemMeta(meta); // Apply changes
                }
            }
        }
    }

    private void sendWarningMessage(Player player) {
        long currentTime = System.currentTimeMillis();
        long lastMessageTime = messageCooldowns.getOrDefault(player, 0L);
        // Check if the cooldown has finished.
        if (currentTime - lastMessageTime >= 5000) {
            player.sendMessage(armourBrokenMessage);
            messageCooldowns.put(player, currentTime);
        }
    }
}