package org.unitedlands.items.crops;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class SoyBean extends CustomCrop {

    public SoyBean() {
        super("soybean",
                List.of("soy_bean_stage_1", "soy_bean_stage_2", "soy_bean_stage_3"),
                "soy_bean_stage_4",
                Set.of(Material.FARMLAND),
                "soy_bean_seeds",
                Set.of(),
                false
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
