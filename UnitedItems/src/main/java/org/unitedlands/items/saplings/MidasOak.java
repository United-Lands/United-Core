package org.unitedlands.items.saplings;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Set;

public class MidasOak extends CustomSapling {

    public MidasOak() {
        super("midas_oak_sapling",
                Material.OAK_SAPLING,
                Material.OAK_LOG, "trees:midas_oak_log", false,
                Material.PAPER, "trees:midas_oak_leaves", "trees:midas_oak_leaves_fruited", false,
                0.25, Set.of());
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