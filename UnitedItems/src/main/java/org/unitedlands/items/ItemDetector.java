package org.unitedlands.items;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.unitedlands.items.armours.*;
import org.unitedlands.items.crops.*;
import org.unitedlands.items.saplings.*;
import org.unitedlands.items.tools.*;
import org.unitedlands.items.util.DataManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.unitedlands.items.util.DataManager.log;

public class ItemDetector implements Listener {

    private final Map<String, CustomArmour> armourSets;
    private final Map<String, CustomTool> toolSets;
    private final Map<String, CustomSapling> saplingSets;
    private final Map<String, CustomCrop> cropSets;
    private static final int ONE_YEAR_TICKS = 630720000;
    private final DataManager dataManager;

    public ItemDetector(Plugin plugin) {
        FileConfiguration config = plugin.getConfig();
        armourSets = new HashMap<>();
        toolSets = new HashMap<>();
        saplingSets = new HashMap<>();
        cropSets = new HashMap<>();
        dataManager = new DataManager();

        armourSets.put("nutcracker", new NutcrackerArmour());
        armourSets.put("gamemaster", new GamemasterArmour(plugin, config));

        toolSets.put("gamemaster", new GamemasterTools(plugin, config));
        toolSets.put("amethyst", new AmethystPickaxe());
        toolSets.put("barkbinder", new BarkbinderAxe());

        saplingSets.put("ancient_oak_sapling", new AncientOak());
        saplingSets.put("avocado_sapling", new Avocado());
        saplingSets.put("banana_sapling", new Banana());
        saplingSets.put("lemon_sapling", new Lemon());
        saplingSets.put("mango_sapling", new Mango());
        saplingSets.put("midas_jungle_sapling", new MidasJungle());
        saplingSets.put("midas_oak_sapling", new MidasOak());
        saplingSets.put("olive_sapling", new Olive());
        saplingSets.put("orange_sapling", new Orange());
        saplingSets.put("pear_sapling", new Pear());

        cropSets.put("bellpepper", new BellPepper());
        cropSets.put("broccoli", new Broccoli());
        cropSets.put("celery", new Celery());
        cropSets.put("chilipepper", new ChiliPepper());
        cropSets.put("cucumber", new Cucumber());
        cropSets.put("garlic", new Garlic());
        cropSets.put("lettuce", new Lettuce());
        cropSets.put("onion", new Onion());
        cropSets.put("pea", new Pea());
        cropSets.put("soybean", new SoyBean());
        cropSets.put("tomato", new Tomato());

        dataManager.loadSaplings(saplingSets);
        dataManager.loadCrops(cropSets);

        Bukkit.getScheduler().runTaskLater(plugin, () -> log("Saplings in memory after load: " + dataManager.getSaplingCount()), 100L);
        Bukkit.getScheduler().runTaskLater(plugin, () -> log("Crops in memory after load: " + dataManager.getCropCount()), 100L);

    }

    /*

    #####################################################
    # +-----------------------------------------------+ #
    # |                Armour Handling                | #
    # +-----------------------------------------------+ #
    #####################################################

    This section contains all methods and events related to armour handling.

     */

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
        return isCustomArmourPiece(helmet, setId) &&
                isCustomArmourPiece(chestplate, setId) &&
                isCustomArmourPiece(leggings, setId) &&
                isCustomArmourPiece(boots, setId);
    }

    // Check if an individual armour piece matches the setId.
    private boolean isCustomArmourPiece(ItemStack item, String setId) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        CustomStack customStack = CustomStack.byItemStack(item);
        return customStack != null && customStack.getId().contains(setId);
    }

    // Apply effects if armour is worn.
    private void applyEffectsIfWearingArmor(Player player) {
        CustomArmour armour = detectArmourSet(player);
        if (armour != null) {
            armour.applyEffects(player);
        } else {
            removeAllEffects(player);
        }
    }

    // Remove effects if armour is removed.
    private void removeAllEffects(Player player) {
        if (detectArmourSet(player) == null) {
            // Remove only the potion effects that were applied by our custom armour.
            for (CustomArmour armour : armourSets.values()) {
                for (PotionEffectType effectType : armour.getAppliedEffects()) {
                    if (player.hasPotionEffect(effectType)) {
                        PotionEffect current = player.getPotionEffect(effectType);
                        // Remove the effect the duration is above one year.
                        if (current != null && current.getDuration() >= ONE_YEAR_TICKS) {
                            player.removePotionEffect(effectType);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    // Check player damage events for use of custom armour.
    public void handlePlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        CustomArmour armour = detectArmourSet(player);
        if (armour != null) {
            armour.handlePlayerDamage(player, event);
        }
    }

    @EventHandler
    // Handle experience pickups.
    public void handleExpPickup(PlayerPickupExperienceEvent event) {
        Player player = event.getPlayer();
        ExperienceOrb orb = event.getExperienceOrb();
        CustomArmour armour = detectArmourSet(player);
        if (armour != null) {
            armour.handleExpPickup(player, orb);
        }
    }

    @EventHandler
    // Handle armour changes.
    public void onPlayerArmorChange(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("UnitedItems")), () -> applyEffectsIfWearingArmor(player));
    }

    @EventHandler
    // Apply or remove effects when a player joins.
    public void onPlayerJoin(PlayerJoinEvent event) {
        applyEffectsIfWearingArmor(event.getPlayer());
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

    /*

    ###################################################
    # +---------------------------------------------+ #
    # |                Tool Handling                | #
    # +---------------------------------------------+ #
    ###################################################

    This section contains all methods and events related to tool and weapon handling.

     */

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

    // Remove any effects if a player is no longer holding the tool.
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
    // Check block breaks for use of custom tools.
    public void onNormalBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        CustomTool tool = detectTool(player);
        // Delegate the block breaking logic to the specific tool.
        if (tool != null) {
            tool.handleBlockBreak(player, event);
        }
    }

    @EventHandler
    // Check entity damage for use of custom tools.
    public void handleEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            CustomTool tool = detectTool(player);
            if (tool != null) {
                tool.handleEntityDamage(player, event);
            }
        }
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

    /*

    ###################################################
    # +---------------------------------------------+ #
    # |                Tree Handling                | #
    # +---------------------------------------------+ #
    ###################################################

    This section contains all methods and events related to trees and saplings.

     */

    // Detect if a held item is a custom sapling.
    public CustomSapling detectSapling(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return null;
        CustomStack customStack = CustomStack.byItemStack(item);
        return (customStack != null) ? saplingSets.get(customStack.getId().toLowerCase()) : null;
    }

    // Handle sapling placement.
    public boolean handleSaplingInteraction(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getItem() == null) {
            return false;
        }

        CustomSapling sapling = detectSapling(event.getItem());
        if (sapling == null) {
            return false;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null ||
                !(clickedBlock.getType() == Material.GRASS_BLOCK ||
                        clickedBlock.getType() == Material.DIRT ||
                        clickedBlock.getType() == Material.PODZOL ||
                        clickedBlock.getType() == Material.SHORT_GRASS ||
                        clickedBlock.getType() == Material.TALL_GRASS ||
                        clickedBlock.getType() == Material.DEAD_BUSH ||
                        clickedBlock.getType() == Material.SNOW)) {
            return false;
        }

        // Check if the clicked block is short grass or tall grass.
        if (clickedBlock.getType() == Material.SHORT_GRASS ||
                clickedBlock.getType() == Material.TALL_GRASS ||
                clickedBlock.getType() == Material.DEAD_BUSH ||
                clickedBlock.getType() == Material.SNOW) {
            clickedBlock.setType(Material.AIR); // Remove the grass
            clickedBlock = clickedBlock.getRelative(0, -1, 0); // Get the block below/
        }

        // Ensure sapling can only be planted on valid ground.
        if (!(clickedBlock.getType() == Material.GRASS_BLOCK || clickedBlock.getType() == Material.DIRT || clickedBlock.getType() == Material.PODZOL)) {
            return false;
        }

        Biome biome = clickedBlock.getBiome();
        if (!sapling.canGrowInBiome(biome)) {
            event.setCancelled(true);
            return true;
        }

        Block above = clickedBlock.getRelative(0, 1, 0);
        if (!above.getType().equals(Material.AIR)) {
            return false;
        }

        // Plant the sapling.
        above.setType(sapling.getVanillaSapling());
        dataManager.addSapling(above.getLocation(), sapling);

        // Reduce the item count.
        event.getItem().setAmount(event.getItem().getAmount() - 1);

        // Cancel the event to prevent further processing.
        event.setCancelled(true);
        return true;
    }

    @EventHandler
    // Handle tree construction.
    public void onGrow(StructureGrowEvent event) {
        Location location = event.getLocation().toBlockLocation();
        CustomSapling sapling = dataManager.getSapling(location);

        if (sapling != null) {
            Biome biome = location.getBlock().getBiome();
            if (!sapling.canGrowInBiome(biome)) {
                event.setCancelled(true);
                return;
            }

            event.setCancelled(true); // Stop the vanilla tree from growing.

            for (BlockState block : event.getBlocks()) {
                Location blockLocation = block.getLocation().toBlockLocation();
                Material blockMaterial = block.getBlockData().getMaterial();

                // Create the stem.
                if (blockMaterial.toString().endsWith("_LOG")) {
                    if (sapling.isUsingVanillaStem()) {
                        Bukkit.getScheduler().runTaskLater(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("UnitedItems")), () ->
                                blockLocation.getBlock().setType(sapling.getStemBlock()), 5L);
                    } else if (sapling.getStemReplaceBlockName() != null) {
                        CustomBlock placedBlock = CustomBlock.place(sapling.getStemReplaceBlockName(), blockLocation);
                        if (placedBlock == null) {
                            blockLocation.getBlock().setType(sapling.getStemBlock());
                        }
                    }
                }

                // Create the leaves.
                else if (block.getType() == Material.OAK_LEAVES || block.getType() == Material.JUNGLE_LEAVES) {
                    if (sapling.getCustomLeavesName() != null) {
                        blockLocation.getBlock().setType(Material.AIR);
                        String leafType = sapling.isSuccessful() ? sapling.getFruitedLeavesName() : sapling.getCustomLeavesName();
                        CustomBlock.place(leafType, blockLocation);
                    }
                }
            }
        }
    }


    @EventHandler
    // Handle custom tree leaf decay.
    public void onDecay(LeavesDecayEvent event) {
        CustomSapling sapling = dataManager.getSapling(event.getBlock().getLocation());
        if (sapling != null) {
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
        }
    }

    @EventHandler
    // Remove blocks from the map if they're broken.
    public void onTreeBlockBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        if (dataManager.hasSapling(loc)) {
            dataManager.removeSapling(loc);
        }
    }

    /*

    ###################################################
    # +---------------------------------------------+ #
    # |                Crop Handling                | #
    # +---------------------------------------------+ #
    ###################################################

    This section contains all methods and events related to crop handling.

     */

    // Detect if a crop is custom and what it is.
    public CustomCrop detectCrop(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return null;
        CustomStack customStack = CustomStack.byItemStack(item);

        if (customStack == null) return null;

        // Check if item is a seed instead of a fully-grown crop.
        for (CustomCrop crop : cropSets.values()) {
            if (customStack.getId().equalsIgnoreCase(crop.getSeedItemId())) {
                return crop; // Return the crop that corresponds to this seed.
            }
        }
        return null;
    }

    // Main method to handle crop interactions.
    public boolean handleCropInteraction(PlayerInteractEvent event) {
        // Only process the event from the main hand to prevent double handling.
        // This fixes a bug that causes a crop harvest when bonemealing at stage 3 growth.
        if (event.getHand() != EquipmentSlot.HAND) return false;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return false;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return false;

        Location loc = clickedBlock.getLocation();

        // First, check if the block is a registered crop.
        if (dataManager.hasCrop(loc)) {
            CustomCrop growingCrop = dataManager.getCrop(loc);
            int growthStage = dataManager.getCropStage(loc);

            ItemStack heldItem = event.getItem();
            boolean isHoldingBonemeal = heldItem != null && heldItem.getType() == Material.BONE_MEAL;

            // Simulate bonemeal growth if holding bonemeal.
            if (isHoldingBonemeal) {
                if (growthStage < growingCrop.getMaxGrowthStage()) {
                    int newStage = growthStage + 1;
                    growingCrop.placeCrop(loc, newStage);
                    dataManager.updateCropStage(loc, newStage);
                    heldItem.setAmount(heldItem.getAmount() - 1);
                    loc.getWorld().spawnParticle(org.bukkit.Particle.HAPPY_VILLAGER, loc.clone().add(0.5, 0.5, 0.5), 10, 0.3, 0.3, 0.3);
                    loc.getWorld().playSound(loc, org.bukkit.Sound.ITEM_BONE_MEAL_USE, 1.0f, 1.0f);
                    event.setCancelled(true);
                    return true;
                }

                // If fully grown, cancel the bonemeal and harvest instead.
                growingCrop.harvestWithoutBreaking(loc, event.getPlayer(), dataManager);
                growingCrop.placeCrop(loc, 1);
                dataManager.updateCropStage(loc, 1);
                growingCrop.startRandomGrowthTask(loc, dataManager);
                loc.getWorld().playSound(loc, org.bukkit.Sound.BLOCK_CROP_BREAK, 1.0f, 1.0f);
                event.setCancelled(true);
                return true;
            }

            // Handle right-clicks when not holding bonemeal (all other situations).
            if (growingCrop.canBeHarvestedWithoutBreaking() && growthStage == growingCrop.getMaxGrowthStage()) {
                growingCrop.harvestWithoutBreaking(loc, event.getPlayer(), dataManager);
                growingCrop.placeCrop(loc, 1);
                dataManager.updateCropStage(loc, 1);
                growingCrop.startRandomGrowthTask(loc, dataManager);
                event.setCancelled(true);
                return true;
            }
        }

        // Handle planting new crops.
        CustomCrop crop = detectCrop(event.getItem());
        if (crop == null) return false;

        if (!crop.canBePlantedOn(clickedBlock.getType())) return false;
        Block above = clickedBlock.getRelative(0, 1, 0);
        if (!above.getType().equals(Material.AIR)) return false;

        crop.placeCrop(above.getLocation(), 1);
        dataManager.addCrop(above.getLocation(), crop, 1);
        above.getWorld().playSound(above.getLocation(), org.bukkit.Sound.ITEM_CROP_PLANT, 1.0f, 1.0f);
        crop.startRandomGrowthTask(above.getLocation(), dataManager);

        Objects.requireNonNull(event.getItem()).setAmount(event.getItem().getAmount() - 1);
        event.setCancelled(true);

        return true;
    }

    @EventHandler
    // Handle breaking crops.
    public void onCropBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        if (!dataManager.hasCrop(loc)) return;
        Player player = event.getPlayer();
        CustomCrop crop = dataManager.getCrop(loc);
        int growthStage = dataManager.getCropStage(loc);

        // If fully grown, harvest it.
        if (crop.isFullyGrown(growthStage)) {
            crop.onHarvest(loc, player);
            dataManager.removeCrop(loc);
        } else {
            // Otherwise, break the crop.
            dataManager.removeCrop(loc);
            loc.getBlock().setType(Material.AIR);
        }
    }

    /*

    ######################################################
    # +------------------------------------------------+ #
    # |                General Handling                | #
    # +------------------------------------------------+ #
    ######################################################

    This section contains all methods and events that are not specific to one category.

     */

    public void saveData() {
        dataManager.saveSaplings();
        dataManager.saveCrops();
    }

    @EventHandler
    // Check block interactions for use of custom items.
    public void handleInteract(PlayerInteractEvent event) {
        // Check if it's a sapling first.
        if (handleSaplingInteraction(event))
            return;
        // Then check if it's a crop.
        if (handleCropInteraction(event))
            return;
        // Continue to tool processing.
        Player player = event.getPlayer();
        CustomTool tool = detectTool(player);
        if (tool != null) {
            tool.handleInteract(player, event);
        }
    }
}