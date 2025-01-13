package org.unitedlands.items.util;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.unitedlands.items.armours.CustomArmour;
import org.unitedlands.items.armours.Nutcracker;
import org.unitedlands.items.tools.CustomTool;
import org.unitedlands.items.tools.Gamemaster;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ItemDetector implements Listener {

    private final Map<String, CustomArmour> armourSets;
    private final Map<String, CustomTool> toolSets;

    public ItemDetector() {
        armourSets = new HashMap<>();
        toolSets = new HashMap<>();
        armourSets.put("nutcracker", new Nutcracker());
        toolSets.put("gamemaster", new Gamemaster());
        // Add more sets here...
    }

    // Detect if the player is wearing a full set of a registered armour.
    private CustomArmour detectArmourSet(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        ItemStack chestplate = player.getInventory().getChestplate();
        ItemStack leggings = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();

        for (Map.Entry<String, CustomArmour> entry : armourSets.entrySet()) {
            String setId = entry.getKey();
            if (isFullSet(helmet, chestplate, leggings, boots, setId)) {
                return entry.getValue();
            }
        }

        // No matching set found
        return null;
    }

    // Check if all pieces of the set match the given setId.
    private boolean isFullSet(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, String setId) {
        return isCustomArmorPiece(helmet, setId) &&
                isCustomArmorPiece(chestplate, setId) &&
                isCustomArmorPiece(leggings, setId) &&
                isCustomArmorPiece(boots, setId);
    }

    // Check if an individual armour piece matches the setId.
    private boolean isCustomArmorPiece(ItemStack item, String setId) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        CustomStack customStack = CustomStack.byItemStack(item);
        return customStack != null && customStack.getId().contains(setId);
    }

    private void applyEffectsIfWearingArmor(Player player) {
        CustomArmour armour = detectArmourSet(player);
        if (armour != null) {
            armour.applyEffects(player);
        } else {
            removeAllEffects(player);
        }
    }

    // Remove only the effects applied by the specific armor
    private void removeAllEffects(Player player) {
        CustomArmour detectedArmour = detectArmourSet(player);
        if (detectedArmour != null) {

            for (PotionEffectType effectType : detectedArmour.getAppliedEffects()) {
                if (player.hasPotionEffect(effectType)) {
                    player.removePotionEffect(effectType);
                }
            }
        } else {
            // Fallback, remove effects that could belong to any armour in the registry.
            armourSets.values().forEach(armourSet -> {
                for (PotionEffectType effectType : armourSet.getAppliedEffects()) {
                    if (player.hasPotionEffect(effectType)) {
                        player.removePotionEffect(effectType);
                    }
                }
            });
        }
    }

    // Detect if the player is holding a registered tool.
    private CustomTool detectTool(Player player) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        for (Map.Entry<String, CustomTool> entry : toolSets.entrySet()) {
            String toolId = entry.getKey();
            if (isCustomTool(itemInHand, toolId)) {
                return entry.getValue();
            }
        }

        // No matching tool found
        return null;
    }

    // Check if the item in hand matches the toolId.
    private boolean isCustomTool(ItemStack item, String toolId) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        CustomStack customStack = CustomStack.byItemStack(item);
        return customStack != null && customStack.getId().contains(toolId);
    }

    // Apply effects for the detected tool.
    private void applyEffectsIfHoldingTool(Player player) {
        CustomTool tool = detectTool(player);
        if (tool != null) {
            tool.applyEffects(player);
        } else {
            removeToolEffects(player);
        }
    }

    private void removeToolEffects(Player player) {
        CustomTool detectedTool = detectTool(player); // Detect currently held tool
        if (detectedTool != null) {
            // Remove only the effects applied by the detected tool
            for (PotionEffectType effectType : detectedTool.getAppliedEffects()) {
                if (player.hasPotionEffect(effectType)) {
                    player.removePotionEffect(effectType);
                }
            }
        } else {
            // Fallback: Remove effects that could belong to any tool in the registry
            toolSets.values().forEach(tool -> {
                for (PotionEffectType effectType : tool.getAppliedEffects()) {
                    if (player.hasPotionEffect(effectType)) {
                        player.removePotionEffect(effectType);
                    }
                }
            });
        }
    }

    @EventHandler
    // Handle armour changes using.
    public void onPlayerArmorChange(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("UnitedItems")), () -> applyEffectsIfWearingArmor(player));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Apply or remove effects when a player joins.
        applyEffectsIfWearingArmor(event.getPlayer());
    }

    @EventHandler
    // Check if tools has been moved when interacting with the inventory.
    public void onInventoryClick(InventoryClickEvent event) {
        Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("UnitedItems")), () -> {
            if (event.getWhoClicked() instanceof Player player) {
                applyEffectsIfHoldingTool(player);
            }
        });
    }

    @EventHandler
    // Check if the armour has broken when taking damage.
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("UnitedItems")), () -> applyEffectsIfWearingArmor(player));
        }
    }

    @EventHandler
    // Removes effects on player death.
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        detectArmourSet(player);
        Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("UnitedItems")), () -> removeAllEffects(player));
    }

    @EventHandler
    // Checks when a player switches their held item.
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("UnitedItems")), () -> applyEffectsIfHoldingTool(player));
    }

    @EventHandler
    // Checks if the dropped item is a registered tool.
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack droppedItem = event.getItemDrop().getItemStack();
        for (Map.Entry<String, CustomTool> entry : toolSets.entrySet()) {
            String toolId = entry.getKey();
            if (isCustomTool(droppedItem, toolId)) {
                // If the dropped item is a registered tool, remove its effects.
                Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("UnitedItems")), () -> removeToolEffects(player));
                break; // Stop checking further since the tool has been identified.
            }
        }
    }

    @EventHandler
    // Checks if the player picks up a registered tool.
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        // Check if the entity picking up the item is a player.
        if (event.getEntity() instanceof Player player) {
            ItemStack pickedUpItem = event.getItem().getItemStack();
            // Check if the picked up item is a registered tool.
            for (Map.Entry<String, CustomTool> entry : toolSets.entrySet()) {
                String toolId = entry.getKey();
                if (isCustomTool(pickedUpItem, toolId)) {
                    // If the picked up item is a registered tool, apply its effects.
                    Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("UnitedItems")), () -> applyEffectsIfHoldingTool(player));
                    break; // Stop checking further since the tool has been identified.
                }
            }
        }
    }
}
