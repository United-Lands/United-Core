package org.unitedlands.items.saplings;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.util.Set;

public class Avocado extends CustomSapling {

    public Avocado() {
        super("avocado_sapling",
                Material.JUNGLE_SAPLING,
                Material.JUNGLE_LOG, null, true,
                Material.PAPER, "trees:jungle_leaves", "trees:avocado_leaves_fruited", false,
                0.25, Set.of(Biome.JUNGLE, Biome.SPARSE_JUNGLE, Biome.SWAMP, Biome.SAVANNA));
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