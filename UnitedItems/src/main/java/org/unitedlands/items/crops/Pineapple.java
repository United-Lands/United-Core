package org.unitedlands.items.crops;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class Pineapple extends CustomCrop {

    public Pineapple() {
        super("pineapple",
                List.of("pineapple_stage_1", "pineapple_stage_2", "pineapple_stage_3"),
                "pineapple_stage_4",
                Set.of(Material.FARMLAND),
                "pineapple",
                Set.of(Biome.JUNGLE, Biome.SPARSE_JUNGLE),
                true
        );
    }

    @Override
    public int getMaxGrowthStage() {
        return 4;
    }

    @Override
    public List<ItemStack> getHarvestDrops() {
        CustomStack customStack = CustomStack.getInstance("food:pineapple");
        ItemStack customItem = customStack.getItemStack();
        customItem.setAmount(1);
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
