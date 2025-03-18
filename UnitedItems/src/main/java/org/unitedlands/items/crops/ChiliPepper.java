package org.unitedlands.items.crops;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class ChiliPepper extends CustomCrop {

    public ChiliPepper() {
        super("chilipepper",
                List.of("chili_pepper_stage_1", "chili_pepper_stage_2", "chili_pepper_stage_3"),
                "chili_pepper_stage_4",
                Set.of(Material.FARMLAND),
                "chili_pepper_seeds",
                Set.of(Biome.DESERT, Biome.SAVANNA, Biome.WINDSWEPT_SAVANNA, Biome.SAVANNA_PLATEAU, Biome.JUNGLE, Biome.SPARSE_JUNGLE),
                true
        );
    }

    @Override
    public int getMaxGrowthStage() {
        return 4;
    }

    @Override
    public List<ItemStack> getHarvestDrops() {
        CustomStack customStack = CustomStack.getInstance("food:chili_pepper");
        ItemStack customItem = customStack.getItemStack();
        customItem.setAmount(2);
        return List.of(customItem);
    }

    @Override
    public void onPlant(Player player, Location location) {
    }

    @Override
    public void onGrow(Location location) {
    }

    @Override
    public void onHarvest(Location location, Player player) {
    }
}
