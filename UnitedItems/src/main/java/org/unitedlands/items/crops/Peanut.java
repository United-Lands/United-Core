package org.unitedlands.items.crops;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class Peanut extends CustomCrop {

    public Peanut() {
        super("peanut",
                List.of("peanut_stage_1", "peanut_stage_2", "peanut_stage_3"),
                "peanut_stage_4",
                Set.of(Material.FARMLAND),
                "peanut",
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
        CustomStack customStack = CustomStack.getInstance("food:peanut");
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
