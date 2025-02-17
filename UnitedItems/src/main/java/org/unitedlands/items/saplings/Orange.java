package org.unitedlands.items.saplings;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Orange extends CustomSapling {

    public Orange() {
        super("orange",
                Material.OAK_SAPLING,
                Material.OAK_LOG, null, true,
                Material.PAPER, "trees:oak_leaves", "trees:orange_leaves_fruited", false,
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