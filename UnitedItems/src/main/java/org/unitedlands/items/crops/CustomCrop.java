package org.unitedlands.items.crops;

import dev.lone.itemsadder.api.CustomBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.unitedlands.items.util.DataManager;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Objects;


public abstract class CustomCrop {

    private final String id;
    private final List<String> growthStages;
    private final String finalStage;
    private final Set<Material> validSoils;
    private final String seedItemId;
    private final Set<Biome> allowedBiomes;
    private static final Random random = new Random();
    private final boolean isPersistentHarvest;

    public CustomCrop(String id, List<String> growthStages, String finalStage, Set<Material> validSoils, String seedItemId, Set<Biome> allowedBiomes, boolean isPersistentHarvest) {
        this.id = id;
        this.growthStages = growthStages;
        this.finalStage = finalStage;
        this.validSoils = validSoils;
        this.seedItemId = seedItemId;
        this.allowedBiomes = allowedBiomes;
        this.isPersistentHarvest = isPersistentHarvest;
    }

    public void startRandomGrowthTask(Location location, DataManager dataManager) {
        int randomDelay = 20 * (5 + random.nextInt(15)); // Random delay between 5-20 seconds

        new BukkitRunnable() {
            @Override
            public void run() {
                // If the crop was removed, cancel growth
                if (!dataManager.hasCrop(location)) {
                    cancel();
                    return;
                }

                CustomCrop crop = dataManager.getCrop(location);
                int growthStage = dataManager.getCropStage(location);

                if (growthStage < crop.getMaxGrowthStage()) {
                    crop.placeCrop(location, growthStage + 1);
                    dataManager.updateCropStage(location, growthStage + 1);

                    // Reschedule another growth cycle with random delay
                    startRandomGrowthTask(location, dataManager);
                } else {
                    cancel(); // Fully grown, stop task
                }
            }
        }.runTaskLater(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("UnitedItems")), randomDelay);
    }

    public String getId() {
        return id;
    }

    public boolean canGrowInBiome(Biome biome) {
        return allowedBiomes == null || allowedBiomes.isEmpty() || allowedBiomes.contains(biome);
    }

    public boolean canBePlantedOn(Material material) {
        return validSoils.contains(material);
    }

    public String getNextGrowthStage(int stage) {
        return (stage < growthStages.size() - 1) ? growthStages.get(stage + 1) : finalStage;
    }

    public boolean isFullyGrown(int stage) {
        return stage >= getMaxGrowthStage();
    }

    public void placeCrop(Location location, int growthStage) {
        int adjustedStage = Math.max(growthStage - 1, 0);
        if (adjustedStage < growthStages.size()) {
            CustomBlock.place(growthStages.get(adjustedStage), location);
        } else {
            CustomBlock.place(finalStage, location);
        }
    }

    public String getSeedItemId() {
        return seedItemId;
    }

    public boolean canBeHarvestedWithoutBreaking() {
        return isPersistentHarvest;
    }

    public void harvestWithoutBreaking(Location location, Player player, DataManager dataManager) {
        if (!canBeHarvestedWithoutBreaking()) return;

        int growthStage = dataManager.getCropStage(location);

        if (growthStage != getMaxGrowthStage()) {
            return;
        }

        for (ItemStack drop : getHarvestDrops()) {
            location.getWorld().dropItemNaturally(location.clone().add(0.5, 0.5, 0.5), drop);
        }

        // Reset to Stage 1
        dataManager.updateCropStage(location, 1);
        placeCrop(location, 1);

        // Restart natural growth.
        Bukkit.getScheduler().runTaskLater(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("UnitedItems")), () -> startRandomGrowthTask(location, dataManager), 20 * 5); // 5-second delay before regrowing starts.
    }

    public abstract List<ItemStack> getHarvestDrops();

    public abstract int getMaxGrowthStage();

    public abstract void onPlant(Player player, Location location);

    public abstract void onGrow(Location location);

    public abstract void onHarvest(Location location, Player player);
}
