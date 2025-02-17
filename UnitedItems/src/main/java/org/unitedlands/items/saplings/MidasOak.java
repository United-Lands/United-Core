package org.unitedlands.items.saplings;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MidasOak extends CustomSapling {

    public MidasOak() {
        super("midas_oak",
                Material.OAK_SAPLING,
                Material.OAK_LOG, "trees:midas_oak_log", false,
                Material.PAPER, "trees:midas_oak_leaves", "trees:midas_oak_leaves_fruited", false,
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