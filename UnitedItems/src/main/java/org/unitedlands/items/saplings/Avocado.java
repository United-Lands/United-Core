package org.unitedlands.items.saplings;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Avocado extends CustomSapling {

    public Avocado() {
        super("avocado",
                Material.JUNGLE_SAPLING,
                Material.JUNGLE_LOG, null, true,
                Material.PAPER, "trees:jungle_leaves", "trees:avocado_leaves_fruited", false,
                0.25);
    }

    @Override
    public void onPlant(Player player, Location location) {
    }

    @Override
    public void onGrow(Location location) {
    }

    @Override
    public void onDecay(Location location) {
    }

    @Override
    public void onBreak(Location location, Player player) {
    }
}