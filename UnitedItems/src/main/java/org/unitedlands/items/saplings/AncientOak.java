package org.unitedlands.items.saplings;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Set;

public class AncientOak extends CustomSapling {

    public AncientOak() {
        super("ancient_oak_sapling",
                Material.OAK_SAPLING,
                Material.OAK_LOG, "trees:ancient_oak_log", false,
                Material.PAPER, "trees:ancient_oak_leaves", "trees:ancient_oak_leaves", false,
                0.00, Set.of());
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
