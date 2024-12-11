package org.unitedlands.items;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CustomArmour implements Listener {

    private static final String REQUIRED_ARMOUR_NAMESPACE = "christmas";
    private static final String REQUIRED_ARMOUR_ID = "nutcracker";

    private final Plugin plugin;

    public CustomArmour(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
// Starts a check for custom armour if a player interacts with their armour slots.
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if the interaction is with an armor slot, hotbar, or involves shift-clicking
        if (event.getSlotType() == InventoryType.SlotType.ARMOR ||
                event.getSlotType() == InventoryType.SlotType.QUICKBAR ||
                event.isShiftClick()) {

            // Run the check after the inventory updates
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (event.getWhoClicked() instanceof Player player) {
                    checkAndApplyEffect(player);
                }
            });
        }
    }

    @EventHandler
    // Starts a check for custom armour if a player interacts with their inventory.
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND) {
            Bukkit.getScheduler().runTask(plugin, () -> checkAndApplyEffect(event.getPlayer()));
        }
    }

    // Checks if it's custom armour using namespace and ID.
    private boolean isCustomArmorPiece(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        CustomStack customStack = CustomStack.byItemStack(item);
        return customStack != null && customStack.getNamespace().equals(REQUIRED_ARMOUR_NAMESPACE) &&
                customStack.getId().contains(REQUIRED_ARMOUR_ID);
    }

    // Checks if all armour slots have custom armour.
    private boolean isWearingNutcrackerArmour(Player player) {
        return isCustomArmorPiece(player.getInventory().getHelmet()) &&
                isCustomArmorPiece(player.getInventory().getChestplate()) &&
                isCustomArmorPiece(player.getInventory().getLeggings()) &&
                isCustomArmorPiece(player.getInventory().getBoots());
    }

    // Checks if the player is flagged as wearing custom armour and applies relevant effects.
    private void checkAndApplyEffect(Player player) {
        if (isWearingNutcrackerArmour(player)) {
            if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true, false));
            }
        } else {
            player.removePotionEffect(PotionEffectType.SPEED);
        }
    }

    //Some general checks for scenarios armour may be added or removed.

    @EventHandler
    // Reapplies effects on join.
    public void onPlayerJoin(PlayerJoinEvent event) {
        checkAndApplyEffect(event.getPlayer());
    }

    @EventHandler
    // Check if the armour has broken after taking damage.
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            Bukkit.getScheduler().runTask(plugin, () -> checkAndApplyEffect(player));
        }
    }

    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Bukkit.getScheduler().runTask(plugin, () -> checkAndApplyEffect(player));
    }
}
