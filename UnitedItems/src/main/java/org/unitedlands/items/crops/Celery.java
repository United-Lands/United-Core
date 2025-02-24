package org.unitedlands.items.crops;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class Celery extends CustomCrop {

    public Celery() {
        super("celery",
                List.of("celery_stage_1", "celery_stage_2", "celery_stage_3"),
                "celery_stage_4",
                Set.of(Material.FARMLAND),
                "celery_seeds",
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
