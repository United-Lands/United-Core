package org.unitedlands.items.saplings;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public abstract class CustomSapling {

    private final String id;
    private final Material vanillaSapling;
    private final Material stemBlock;
    private final String stemReplaceBlockName;
    private final Material fruitBlock;
    private final String customLeavesName;
    private final String fruitedLeavesName;
    private final double fruitChance;
    private final boolean useVanillaStem;
    private final boolean useVanillaLeaves;
    private final Set<Biome> allowedBiomes;

    public CustomSapling(String id, Material vanillaSapling, Material stemBlock,
                         String stemReplaceBlockName, boolean useVanillaStem,
                         Material fruitBlock, String customLeavesName, String fruitedLeavesName, boolean useVanillaLeaves,
                         double fruitChance, Set<Biome> allowedBiomes) {
        this.id = id;
        this.vanillaSapling = vanillaSapling;
        this.stemBlock = stemBlock;
        this.stemReplaceBlockName = stemReplaceBlockName;
        this.useVanillaStem = useVanillaStem;
        this.fruitBlock = fruitBlock;
        this.customLeavesName = customLeavesName;
        this.fruitedLeavesName = fruitedLeavesName;
        this.useVanillaLeaves = useVanillaLeaves;
        this.fruitChance = fruitChance;
        this.allowedBiomes = allowedBiomes;
    }

    public boolean isUsingVanillaStem() {
        return useVanillaStem;
    }

    public boolean isUsingVanillaLeaves() {
        return useVanillaLeaves;
    }

    public String getId() {
        return id.toLowerCase();
    }

    public ItemStack getSeedItem() {
        CustomStack customStack = CustomStack.getInstance(id);
        return customStack != null ? customStack.getItemStack() : new ItemStack(Material.AIR);
    }

    public Material getVanillaSapling() {
        return vanillaSapling;
    }

    public Material getStemBlock() {
        return stemBlock;
    }

    public String getStemReplaceBlockName() {
        return stemReplaceBlockName;
    }

    public Material getFruitBlock() {
        return fruitBlock;
    }

    public String getCustomLeavesName() {
        return customLeavesName;
    }

    public String getFruitedLeavesName() {
        return fruitedLeavesName;
    }

    public boolean isSuccessful() {
        return Math.random() < fruitChance;
    }

    public boolean hasPermission(Player player) {
        return true;
    }

    public boolean canGrowInBiome(Biome biome) {
        return allowedBiomes == null || allowedBiomes.isEmpty() || allowedBiomes.contains(biome);
    }

    public abstract void onPlant(Player player, Location location);

    public abstract void onGrow(Location location);

    public abstract void onDecay(Location location);

    public abstract void onBreak(Location location, Player player);
}
