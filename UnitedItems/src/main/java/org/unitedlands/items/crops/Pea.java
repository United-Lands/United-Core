package org.unitedlands.items.crops;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class Pea extends CustomCrop {

    public Pea() {
        super("pea",
                List.of("peas_stage_1", "peas_stage_2", "peas_stage_3"),
                "peas_stage_4",
                Set.of(Material.FARMLAND),
                "pea_seeds",
                Set.of(Biome.PLAINS, Biome.FOREST, Biome.BIRCH_FOREST, Biome.FLOWER_FOREST, Biome.TAIGA),
                false
        );
    }

    @Override
    public int getMaxGrowthStage() {
        return 4;
    }

    @Override
    public List<ItemStack> getHarvestDrops() {
        CustomStack customStack = CustomStack.getInstance("food:peas");
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
