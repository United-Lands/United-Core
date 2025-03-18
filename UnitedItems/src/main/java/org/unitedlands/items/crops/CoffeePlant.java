package org.unitedlands.items.crops;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class CoffeePlant extends CustomCrop {

    public CoffeePlant() {
        super("coffeeplant",
                List.of("coffee_plant_stage_1", "coffee_plant_stage_2", "coffee_plant_stage_3"),
                "coffee_plant_stage_4",
                Set.of(Material.FARMLAND),
                "coffee_bean",
                Set.of(Biome.JUNGLE, Biome.SPARSE_JUNGLE, Biome.DESERT),
                true
        );
    }

    @Override
    public int getMaxGrowthStage() {
        return 4;
    }

    @Override
    public List<ItemStack> getHarvestDrops() {
        CustomStack customStack = CustomStack.getInstance("food:coffee_bean");
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
