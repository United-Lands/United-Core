package org.unitedlands.items.crops;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class Grapes extends CustomCrop {

    public Grapes() {
        super("grapes",
                List.of("grapes_stage_1", "grapes_stage_2", "grapes_stage_3"),
                "grapes_stage_4",
                Set.of(Material.FARMLAND),
                "grapes_seeds",
                Set.of(Biome.PLAINS, Biome.FOREST, Biome.FLOWER_FOREST, Biome.BIRCH_FOREST, Biome.SAVANNA, Biome.WINDSWEPT_SAVANNA, Biome.SAVANNA_PLATEAU),
                true
        );
    }

    @Override
    public int getMaxGrowthStage() {
        return 4;
    }

    @Override
    public List<ItemStack> getHarvestDrops() {
        CustomStack customStack = CustomStack.getInstance("food:grapes");
        ItemStack customItem = customStack.getItemStack();
        customItem.setAmount(3);
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
