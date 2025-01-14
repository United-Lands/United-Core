package org.unitedlands.items.tools;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;

public class AmethystPickaxe extends CustomTool implements Listener {

    @Override
    public void applyEffects(Player player) {
    }

    @Override
    public List<PotionEffectType> getAppliedEffects() {
        return Collections.emptyList();
    }

    public void handleBlockBreak(Player player, BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.BUDDING_AMETHYST) {
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.BUDDING_AMETHYST));
        }
    }
}
