package org.unitedlands.items.crops;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class Strawberry extends CustomCrop {

    public Strawberry() {
        super("strawberry",
                List.of("strawberry_stage_1", "strawberry_stage_2", "strawberry_stage_3"),
                "strawberry_stage_4",
                Set.of(Material.FARMLAND),
                "strawberry_seeds",
                Set.of(Biome.PLAINS, Biome.FOREST, Biome.BIRCH_FOREST, Biome.FLOWER_FOREST, Biome.TAIGA),
                true
        );
    }

    @Override
    public int getMaxGrowthStage() {
        return 4;
    }

    @Override
    public List<ItemStack> getHarvestDrops() {
        CustomStack customStack = CustomStack.getInstance("food:strawberry");
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
