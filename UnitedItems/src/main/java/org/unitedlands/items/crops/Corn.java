package org.unitedlands.items.crops;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class Corn extends CustomCrop {

    public Corn() {
        super("corn",
                List.of("corn_stage_1", "corn_stage_2", "corn_stage_3"),
                "corn_stage_4",
                Set.of(Material.FARMLAND),
                "corn_seeds",
                Set.of(Biome.PLAINS, Biome.FOREST, Biome.BIRCH_FOREST, Biome.FLOWER_FOREST, Biome.SAVANNA, Biome.SAVANNA_PLATEAU),
                false);
    }

    @Override
    public int getMaxGrowthStage() {
        return 4;
    }

    @Override
    public List<ItemStack> getHarvestDrops() {
        CustomStack customStack = CustomStack.getInstance("food:corn");
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
