package org.unitedlands.items.util;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.unitedlands.items.armours.CustomArmour;
import org.unitedlands.items.armours.Nutcracker;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ItemDetector implements Listener {

    private final Map<String, CustomArmour> armourSets;

    public ItemDetector() {
        armourSets = new HashMap<>();
        armourSets.put("nutcracker", new Nutcracker());
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
            removeAllEffects(player); // Simplified
        }
    }

    private void removeAllEffects(Player player) {
        CustomArmour detectedArmour = detectArmourSet(player); // Detect currently worn armor
        if (detectedArmour != null) {
            // Remove only the effects applied by the specific armor
            for (PotionEffectType effectType : detectedArmour.getAppliedEffects()) {
                if (player.hasPotionEffect(effectType)) {
                    player.removePotionEffect(effectType);
                }
            }
        } else {
            // Fallback: Remove effects that could belong to any armor in the registry
            armourSets.values().forEach(armourSet -> {
                for (PotionEffectType effectType : armourSet.getAppliedEffects()) {
                    if (player.hasPotionEffect(effectType)) {
                        player.removePotionEffect(effectType);
                    }
                }
            });
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Apply or remove effects when a player joins.
        applyEffectsIfWearingArmor(event.getPlayer());
    }

    @EventHandler
    // Check if armour has been moved when interacting with the inventory.
    public void onInventoryClick(InventoryClickEvent event) {
        Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("UnitedItems")), () -> {
            if (event.getWhoClicked() instanceof Player player) {
                applyEffectsIfWearingArmor(player);
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
}
