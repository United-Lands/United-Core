package org.unitedlands.items.saplings;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MidasJungle extends CustomSapling {

    public MidasJungle() {
        super("midas_jungle",
                Material.JUNGLE_SAPLING,
                Material.JUNGLE_LOG, "trees:midas_jungle_log", false,
                Material.PAPER, "trees:midas_jungle_leaves", false,
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