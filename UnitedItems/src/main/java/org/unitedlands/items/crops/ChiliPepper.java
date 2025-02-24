package org.unitedlands.items.crops;

import org.bukkit.Location;
import org.bukkit.Material;
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
                Set.of(),
                true
        );
    }

    @Override
    public int getMaxGrowthStage() {
        return 4;
    }

    @Override
    public List<ItemStack> getHarvestDrops() {
        return List.of(new ItemStack(Material.DIRT, 4));
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
