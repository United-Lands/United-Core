package org.unitedlands.items.crops;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class Cucumber extends CustomCrop {

    public Cucumber() {
        super("cucumber",
                List.of("cucumber_stage_1", "cucumber_stage_2", "cucumber_stage_3"),
                "cucumber_stage_4",
                Set.of(Material.FARMLAND),
                "cucumber_seeds",
                Set.of(Biome.PLAINS, Biome.FOREST, Biome.FLOWER_FOREST, Biome.BIRCH_FOREST, Biome.JUNGLE, Biome.SPARSE_JUNGLE),
                false
        );
    }

    @Override
    public int getMaxGrowthStage() {
        return 4;
    }

    @Override
    public List<ItemStack> getHarvestDrops() {
        CustomStack customStack = CustomStack.getInstance("food:cucumber");
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
