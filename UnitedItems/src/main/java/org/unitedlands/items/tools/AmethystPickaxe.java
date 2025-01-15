package org.unitedlands.items.tools;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class AmethystPickaxe extends CustomTool implements Listener {

    public void handleBlockBreak(Player player, BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.BUDDING_AMETHYST) {
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.BUDDING_AMETHYST));
        }
    }
}
