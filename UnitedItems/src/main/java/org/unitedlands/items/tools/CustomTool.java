package org.unitedlands.items.tools;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

public abstract class CustomTool {
    // Apply effects to the player
    public abstract void applyEffects(Player player);

    // Get the list of potion effects applied by this tool
    public abstract List<PotionEffectType> getAppliedEffects();

    // Handle block break logic for the tool
    public abstract void handleBlockBreak(Player player, BlockBreakEvent event);
}
